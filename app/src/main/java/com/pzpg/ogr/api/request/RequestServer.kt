package com.pzpg.ogr.api.request

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Class for request to server
 *
 * @property[serverUrl] the url of the server
 * @author github.com/Graidaris
 */
class RequestServer(private val serverUrl: String){

    /**
     * Authorizes a user on the server to use the API
     *
     * @param[account] a google account has GoogleSignInAccount type
     * @return JSONObject which has three keys "mail", "jwtToken" and "refreshToken"
     * this three keys have string value
     * @throws BadRequestException
     * @throws UnauthorizedException
     * @throws NotAllowedMethodException
     *
     * @author github.com/Graidaris
     */
    suspend fun authorize(account: GoogleSignInAccount): JSONObject{
        val endpoint = "/users/authenticate"

        val msg = JSONObject()
        msg.accumulate("idToken", account.idToken)
        msg.accumulate("authCode", account.serverAuthCode)

        return withContext(Dispatchers.IO) {
            val (request, response, result) = Fuel.post(serverUrl + endpoint)
                .header(Headers.CONTENT_TYPE to "application/json")
                .body(msg.toString())
                .responseString()

            Log.i("authorize", response.statusCode.toString())

            when(response.statusCode){
                200 -> return@withContext JSONObject(result.get())
                400 -> throw BadRequestException("Not valid data in request")
                401 -> throw UnauthorizedException("User not authorized")
                405 -> throw NotAllowedMethodException("Bad request method")
                else -> throw RequestServerException("Status code: ${response.statusCode}")
            }
        }
    }

    /**
     * Suspend method to refresh token method to refresh the access token "jwtToken" when the time of the token has expired
     *
     * @param[rToken] refresh token which has string type
     * @return JSONObject which has three keys "mail", "jwtToken" and "refreshToken"
     * this three keys have string value
     * @throws BadRequestException
     * @throws UnauthorizedException
     * @throws NotAllowedMethodException
     *
     * @author github.com/Graidaris
     */
    suspend fun refreshToken(rToken: String): JSONObject{
        val endpoint = "/users/refresh-token"

        return withContext(Dispatchers.IO) {
            val body = JSONObject()
            body.accumulate("token", rToken)

            val (request, response, result) = Fuel.post(serverUrl + endpoint)
                .header(Headers.CONTENT_TYPE to "application/json")
                .body(body.toString())
                .responseString()

            Log.i("refreshToken", response.statusCode.toString())
            when(response.statusCode){
                200 -> return@withContext JSONObject(result.get())
                400 -> throw BadRequestException("Not valid data in request")
                401 -> throw UnauthorizedException("User not authorized")
                405 -> throw NotAllowedMethodException("Bad request method")
                else -> throw RequestServerException("Status code: ${response.statusCode}")
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
     * @throws BadRequestException
     * @throws UnauthorizedException
     * @throws NotAllowedMethodException
     *
     * @author github.com/Graidaris
     */
    suspend fun uploadImage(dir: String, name: String, jwtToken: String): String{
        val endpoint = "/api/graphs/create"

        return withContext(Dispatchers.IO){
            val file = FileDataPart.from(dir, name, name="file")

            Log.i("uploadImage", file.toString())
            val (request , response, result) = Fuel.upload(serverUrl + endpoint)
                .add(file)
                .header(mapOf("authorization" to "Bearer $jwtToken"))
                .responseString()
            val resultToJson = JSONObject(result.get())
            Log.i("uploadImage", response.statusCode.toString())
            when(response.statusCode){
                200 -> return@withContext resultToJson.getString("guid")
                400 -> throw BadRequestException("Not valid data in request")
                401 -> throw UnauthorizedException("User not authorized")
                405 -> throw NotAllowedMethodException("Bad request method")
                else -> throw RequestServerException("Status code: ${response.statusCode}")
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
     * @throws NotAllowedMethodException
     *
     * @author github.com/Graidaris
     */
    suspend fun processImage(guid: String, jwtToken: String, mode: ProcessMode?, format: GraphFormat?) : File? {
        val endpoint = "/api/graphs/process/"

        return withContext(Dispatchers.IO){

            val parameters = ArrayList<Pair<String, String>>()
            parameters.add("mode" to (mode?.name ?: ProcessMode.GRID_BG.name))
            parameters.add("format" to (format?.name ?: GraphFormat.GraphML.name))

            val tempFile = File.createTempFile("temp", ".tmp")

            val (request, response, result) =  Fuel.download(serverUrl + endpoint + guid,
                parameters = parameters, method=Method.POST)
                .fileDestination{
                        _, _ -> tempFile
                }
                .header(mapOf("authorization" to "Bearer $jwtToken"))
                .responseString()

            Log.i("processImage", response.statusCode.toString())
            when(response.statusCode){
                200 -> return@withContext tempFile
                400 -> throw BadRequestException("Not valid data in request")
                401 -> throw UnauthorizedException("User not authorized")
                405 -> throw NotAllowedMethodException("Bad request method")
                else -> throw RequestServerException("Status code: ${response.statusCode}")
            }
        }
    }

    /**
     * Suspend method to download a file which representations a graph which has been recognitions by the server
     *
     * @param[guid] a guid of the uploaded image which we want to process
     * @param[jwtToken] access token gotten from [refreshToken] method or [authorize]
     * @param[format] recognized graph format which we want to take after processing
     * @return temporary file (graph)
     * @throws BadRequestException
     * @throws UnauthorizedException
     * @throws NotAllowedMethodException
     *
     * @author github.com/Graidaris
     */
    suspend fun downloadGraph(guid: String, format: GraphFormat?, jwtToken: String ): File?{

        val endpoint = "/api/graphs/get/"

        return withContext(Dispatchers.IO){
            val parameters = ArrayList<Pair<String, String>>()

            parameters.add("format" to (format?.name ?: GraphFormat.GraphML.name))

            val tempFile = File.createTempFile("temp", ".tmp")

            val (request, response, result) =  Fuel.download(serverUrl + endpoint + guid,
                parameters = parameters)
                .fileDestination{
                        _, _ -> tempFile
                }.header(mapOf("authorization" to "Bearer $jwtToken"))
                .responseString()

            Log.i("downloadGraph", response.statusCode.toString())

            when(response.statusCode){
                200 -> return@withContext tempFile
                400 -> throw BadRequestException("Not valid data in request")
                401 -> throw UnauthorizedException("User not authorized")
                405 -> throw NotAllowedMethodException("Bad request method")
                else -> throw RequestServerException("Status code: ${response.statusCode}")
            }
        }
    }

    suspend fun getHistory(jwtToken: String): JSONArray{
        val endpoint = "/api/graphs/history"

        return withContext(Dispatchers.IO){
            val (request, response, result) = Fuel.get(serverUrl + endpoint)
                .header(mapOf("authorization" to "Bearer $jwtToken"))
                .responseString()

            Log.i("getHistory", response.statusCode.toString())

            when(response.statusCode){
                200 -> return@withContext JSONArray(result.get())
                400 -> throw BadRequestException("Not valid data in request")
                401 -> throw UnauthorizedException("User not authorized")
                405 -> throw NotAllowedMethodException("Bad request method")
                else -> throw RequestServerException("Status code: ${response.statusCode}")
            }
        }
    }


}
