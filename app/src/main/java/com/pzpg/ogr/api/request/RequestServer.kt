package com.pzpg.ogr.api.request

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Headers
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Class responsible for request to server
 *
 * @property[serverUrl] the url of the server
 *
 * @author Władysław Jakołcewicz
 */
class RequestServer(private val serverUrl: String) {

    /**
     * Authorizes a user on the server to use the API
     *
     * @param[account] a google account has GoogleSignInAccount type
     * @return JSONObject which has three keys "mail", "jwtToken" and "refreshToken"
     * this three keys have string value
     * @throws BadRequestException
     * @throws TimeOutException
     * @throws RequestServerException
     *
     * @author Władysław Jakołcewicz
     */
    suspend fun authorize(account: GoogleSignInAccount): JSONObject = withContext(Dispatchers.IO) {
        val endpoint = "/users/authenticate"
        Log.i("authorize", serverUrl + endpoint)
        val msg = JSONObject()
        msg.accumulate("idToken", account.idToken)
        msg.accumulate("authCode", account.serverAuthCode)

        val (request, response, result) = Fuel.post(serverUrl + endpoint)
            .header(Headers.CONTENT_TYPE to "application/json")
            .body(msg.toString())
            .responseString()

        Log.i("authorize", response.statusCode.toString())

        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                throw errorManage("authorize", result, response.statusCode)
            }
            is com.github.kittinunf.result.Result.Success -> {
                val data = result.get()
                val resultToJson = JSONObject(data)
                Log.i("authorize", response.statusCode.toString())
                return@withContext resultToJson
            }
        }
    }

    /**
     * Suspend method to refresh token method to refresh the access token "jwtToken" when the time
     * of the token has expired.
     *
     * @param[rToken] refresh token which has string type
     * @return JSONObject which has three keys "mail", "jwtToken" and "refreshToken"
     * this three keys have string value
     * @throws BadRequestException
     * @throws TimeOutException
     * @throws RequestServerException
     *
     * @author Władysław Jakołcewicz
     */
    suspend fun refreshToken(rToken: String): JSONObject = withContext(Dispatchers.IO) {
        val endpoint = "/users/refresh-token"
        Log.i("refreshToken", serverUrl + endpoint)
        val body = JSONObject()
        body.accumulate("token", rToken)

        val (request, response, result) = Fuel.post(serverUrl + endpoint)
            .header(Headers.CONTENT_TYPE to "application/json")
            .body(body.toString())
            .responseString()

        Log.i("refreshToken", response.statusCode.toString())

        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                throw errorManage("refreshToken", result, response.statusCode)
            }
            is com.github.kittinunf.result.Result.Success -> {
                val data = result.get()
                val resultToJson = JSONObject(data)
                Log.i("refreshToken", response.statusCode.toString())
                return@withContext resultToJson
            }
        }
    }


    /**
     * Suspend method to upload an image which will processed to the server
     *
     * @param[dir] a directory where the image is located, has String type
     * @param[name] a name of the image has String type
     * @param[jwtToken] access token gotten from [refreshToken] method or [authorize]
     * @return guid of the uploaded image, has String type
     *
     * @throws BadRequestException
     * @throws UnauthorizedException
     * @throws TimeOutException
     * @throws RequestServerException
     *
     * @author Władysław Jakołcewicz
     */
    suspend fun uploadImage(
        dir: String,
        name: String,
        jwtToken: String
    ): String = withContext(Dispatchers.IO) {
        val endpoint = "/api/graphs/create"
        Log.i("uploadImage", serverUrl + endpoint)
        val file = FileDataPart.from(dir, name, name = "file")

        Log.i("uploadImage", file.toString())

        val (request, response, result) =
            Fuel.upload(serverUrl + endpoint)
                .add(file)
                .header(mapOf("authorization" to "Bearer $jwtToken"))
                .responseString()

        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                throw errorManage("uploadImage", result, response.statusCode)
            }
            is com.github.kittinunf.result.Result.Success -> {
                val data = result.get()
                val resultToJson = JSONObject(data)
                Log.i("uploadImage", response.statusCode.toString())
                return@withContext resultToJson.getString("guid")
            }
        }
    }

    /**
     * Suspend method to process a image for graph recognition
     *
     * @param[guid] a guid of the uploaded image which we want to process
     * @param[jwtToken] access token gotten from [refreshToken] method or [authorize]
     * @param[mode] a mode of the recognition
     * @param[format] recognized graph format which we want to take after processing
     * @return temporary file (graph)
     * @throws BadRequestException
     * @throws UnauthorizedException
     * @throws TimeOutException
     * @throws RequestServerException
     *
     * @author Władysław Jakołcewicz
     */
    suspend fun processImage(
        guid: String,
        jwtToken: String,
        mode: ProcessMode?
    ) = withContext(Dispatchers.IO) {
        val endpoint = "/api/graphs/process/"
        Log.i("processImage", serverUrl + endpoint)


        val body = JSONObject()
        body.accumulate("mode", mode?.name ?: ProcessMode.GRID_BG.name)


        val (request, response, result) = Fuel.post(serverUrl + endpoint + guid)
            .timeoutRead(1000000)
            .body(body.toString())
            .header(Headers.CONTENT_TYPE to "application/json")
            .header(mapOf("authorization" to "Bearer $jwtToken"))
            .responseString()

        Log.i("processImage", response.statusCode.toString())

        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                throw errorManage("processImage", result, response.statusCode)
            }
            is com.github.kittinunf.result.Result.Success -> {
                Log.i("processImage", response.statusCode.toString())
            }
        }

    }

    /**
     * Suspend method to download a file which representations a graph which has been recognitions by the server
     *
     * @param[guid] a guid of the uploaded image which we want to process
     * @param[jwtToken] access token gotten from [refreshToken] method or [authorize]
     * @param[format] recognized graph format which we want to take after processing
     * @return [File] temporary file (graph)
     * @throws BadRequestException
     * @throws UnauthorizedException
     * @throws TimeOutException
     * @throws RequestServerException
     *
     * @author Władysław Jakołcewicz
     */
    suspend fun downloadGraph(
        guid: String,
        format: GraphFormat?,
        jwtToken: String
    ): File? = withContext(Dispatchers.IO) {

        val endpoint = "/api/graphs/get/"
        val parameters = ArrayList<Pair<String, String>>()

        parameters.add("format" to (format?.name ?: GraphFormat.GraphML.name))

        val tempFile = File.createTempFile("temp", ".tmp")

        val (request, response, result) = Fuel.download(
            serverUrl + endpoint + guid,
            parameters = parameters
        )
            .fileDestination { _, _ ->
                tempFile
            }.header(mapOf("authorization" to "Bearer $jwtToken"))
            .responseString()

        Log.i("downloadGraph", response.statusCode.toString())

        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                throw errorManage("downloadGraph", result, response.statusCode)
            }
            is com.github.kittinunf.result.Result.Success -> {
                Log.i("downloadGraph", response.statusCode.toString())
                return@withContext tempFile
            }
        }

    }


    /**
     * Suspend method to get history from the server about the user activity
     *
     * @param[jwtToken] access token gotten from [refreshToken] method or [authorize]
     * @return[JSONArray] array of entities where contains name, guid, ownersEmail, createdAt, isPublic
     * @throws BadRequestException
     * @throws UnauthorizedException
     * @throws TimeOutException
     * @throws RequestServerException
     *
     * @author Władysław Jakołcewicz
     */
    suspend fun getHistory(jwtToken: String): JSONArray = withContext(Dispatchers.IO) {
        val endpoint = "/api/graphs/history"

        val (request, response, result) = Fuel.get(serverUrl + endpoint)
            .header(mapOf("authorization" to "Bearer $jwtToken"))
            .responseString()

        Log.i("getHistory", response.statusCode.toString())

        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                throw errorManage("getHistory", result, response.statusCode)
            }
            is com.github.kittinunf.result.Result.Success -> {
                val data = result.get()
                val resultToJson = JSONArray(data)
                Log.i("getHistory", response.statusCode.toString())
                return@withContext resultToJson
            }
        }
    }


    /**
     * Suspend method to delete all entities with processed graphs includes image, graphMl, g6 files
     *
     * @param[jwtToken] access token gotten from [refreshToken] method or [authorize]
     * @return[JSONArray] array of entities where contains name, guid, ownersEmail, createdAt, isPublic
     * @throws BadRequestException
     * @throws UnauthorizedException
     * @throws TimeOutException
     * @throws RequestServerException
     *
     * @author Władysław Jakołcewicz
     */
    suspend fun deleteAll(jwtToken: String) = withContext(Dispatchers.IO) {
        val endpoint = "/api/graphs/delete-all"

        Log.i("request to", serverUrl + endpoint)
        val (request, response, result) = Fuel.delete(serverUrl + endpoint)
            .header(mapOf("Authorization" to "Bearer $jwtToken"))
            .header(mapOf("accept" to "*/*"))
            .responseString()

        Log.i("deleteAll", response.statusCode.toString())

        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                throw errorManage("deleteAll", result, response.statusCode)
            }
            is com.github.kittinunf.result.Result.Success -> {
                Log.i("deleteAll", "Success")
            }
        }
    }


    /**
     * Suspend method to delete one of entities with processed graphs includes image, graphMl, g6 files
     *
     * @param[guid] a guid of the uploaded image which we want to process
     * @param[jwtToken] access token gotten from [refreshToken] method or [authorize]
     *
     * @return[JSONArray] array of entities where contains name, guid, ownersEmail, createdAt, isPublic
     *
     * @throws BadRequestException
     * @throws UnauthorizedException
     * @throws TimeOutException
     * @throws RequestServerException
     *
     * @author Władysław Jakołcewicz
     */
    suspend fun deleteData(guid: String, jwtToken: String) = withContext(Dispatchers.IO) {
        val endpoint = "/api/graphs/delete/"

        Log.i("request to", serverUrl + endpoint)
        val (request, response, result) = Fuel.delete(serverUrl + endpoint + guid)
            .header(mapOf("Authorization" to "Bearer $jwtToken"))
            .header(mapOf("accept" to "*/*"))
            .responseString()

        Log.i("deleteData", response.statusCode.toString())

        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                throw errorManage("deleteData", result, response.statusCode)
            }
            is com.github.kittinunf.result.Result.Success -> {
                Log.i("deleteData", "delete $guid - Success")
            }
        }
    }


    /**
     * Suspend method to revoke token when user logged out
     *
     * @param[jwtToken] access token gotten from [refreshToken] method or [authorize]
     * @param[rToken] refresh token gotten when user authorize or refresh jwtToken
     *
     * @throws BadRequestException
     * @throws UnauthorizedException
     * @throws TimeOutException
     * @throws RequestServerException
     *
     * @author Władysław Jakołcewicz
     */
    suspend fun revokeToken(jwtToken: String, rToken: String) = withContext(Dispatchers.IO) {
        val endpoint = "/users/revoke-token"
        Log.i("request to", serverUrl + endpoint)

        val body = JSONObject()
        body.accumulate("token", rToken)

        val (request, response, result) = Fuel.post(serverUrl + endpoint)
            .header(mapOf("Authorization" to "Bearer $jwtToken"))
            .header(Headers.CONTENT_TYPE to "application/json")
            .body(body.toString())
            .responseString()

        Log.i("revokeToken", response.statusCode.toString())

        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                throw errorManage("revokeToken", result, response.statusCode)
            }
            is com.github.kittinunf.result.Result.Success -> {
                Log.i("revokeToken", "Success")
            }
        }
    }


    /**
     * Method to management of exceptions thrown when errors are present in requests from the serverю
     *
     * @param[tag] tag to be shown in logs
     * @param[result] Error result from the response
     * @param[statusCode] response status code
     *
     * @throws BadRequestException
     * @throws UnauthorizedException
     * @throws TimeOutException
     * @throws RequestServerException
     *
     * @return[RequestServerException]
     *
     * @author Władysław Jakołcewicz
     */
    private fun errorManage(
        tag: String,
        result: com.github.kittinunf.result.Result.Failure<FuelError>,
        statusCode: Int
    ): Throwable {
        val ex = result.getException()
        Log.e(tag, result.error.message.toString())
        when (statusCode) {
            400 -> throw BadRequestException("Invalid data in request")
            401 -> throw UnauthorizedException("User not authorized")
            -1 -> throw TimeOutException("Server is not available")
            else -> throw RequestServerException("Fuel ERROR: ${ex.message}")
        }
    }

}
