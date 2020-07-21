package com.pzpg.ogr.api.request

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import kotlin.math.log

class RequestServer(url_server: String){
    private val TAG = "RequestServer"
    private val SERVER_URL = url_server

    suspend fun authorize(account: GoogleSignInAccount): MutableMap<String, String>?{
        val endpoint = "/users/authenticate"

        val msg = JSONObject()
        msg.accumulate("idToken", account.idToken)
        msg.accumulate("authCode", account.serverAuthCode)

        return withContext(Dispatchers.IO) {
            try{
                val (_, response, result) = Fuel.post(SERVER_URL + endpoint)
                    .header(Headers.CONTENT_TYPE to "application/json")
                    .body(msg.toString())
                    .responseString()

                Log.i("authorize", response.statusCode.toString())

                when(response.statusCode){
                    200 -> {
                        val res = JSONObject(result.get())

                        return@withContext mutableMapOf<String, String>().also {
                            it.put("refreshToken", res.getString("refreshToken"))
                            it.put("jwtToken", res.getString("jwtToken"))
                            it.put("code_response",response.statusCode.toString())
                            it.put("info", "success")
                        }
                    }
                    else -> {
                        val (_, error) = result

                        return@withContext mutableMapOf<String, String>().also {
                            it.put("refreshToken", "null")
                            it.put("jwtToken", "null")
                            it.put("code_response",response.statusCode.toString())
                            it.put("info", error.toString().split('\n')[0])
                        }
                    }
                }

            }catch (e: Exception){
                Log.e("authenticate", e.toString())
                return@withContext null
            }
        }
    }

    suspend fun refreshToken(rToken: String): MutableMap<String, String>?{
        val endpoint = "/users/refresh-token"

        return withContext(Dispatchers.IO) {
            try {
                val (_, response, result) = Fuel.post(SERVER_URL + endpoint)
                    .header(Headers.COOKIE to "refreshToken=$rToken; path=/")
                    .responseString()
                Log.i("refreshToken", response.statusCode.toString())
                when(response.statusCode){
                    200 -> {
                        val res = JSONObject(result.get())

                        return@withContext mutableMapOf<String, String>().also {
                            it.put("refreshToken", res.getString("refreshToken"))
                            it.put("jwtToken", res.getString("jwtToken"))
                            it.put("code_response",response.statusCode.toString())
                            it.put("info", "success")
                        }
                    }
                    else -> {
                        val (_, error) = result

                        return@withContext mutableMapOf<String, String>().also {
                            it.put("refreshToken", "null")
                            it.put("jwtToken", "null")
                            it.put("code_response",response.statusCode.toString())
                            it.put("info", error.toString().split('\n')[0])
                        }
                    }
                }

            }catch (e: Exception){
                Log.e("refreshToken", e.toString())
                return@withContext null
            }
        }
    }

    suspend fun processImage(path: String, name: String, jwtToken: String) : MutableMap<String, String>? {
        val endpoint = "/api/image/process"

        return withContext(Dispatchers.IO){
            try {
                val file = FileDataPart.from(path, name, name="file")

                Log.i("processImage", file.toString())
                val (_ , response, result) = Fuel.upload(SERVER_URL + endpoint)
                    .add(file)
                    .header(mapOf("authorization" to "Bearer $jwtToken"))
                    .responseString()

                Log.i("processImage", response.statusCode.toString())

                when(response.statusCode){
                    200 -> {
                        val resultJson = JSONObject(result.get())

                        return@withContext mutableMapOf<String, String>().also {
                            it.put("guid", resultJson.getString("guid"))
                            it.put("code_response",response.statusCode.toString())
                            it.put("info", "success")
                        }
                    }
                    else -> {
                        return@withContext mutableMapOf<String, String>().also {
                            it.put("guid", "null")
                            it.put("code_response",response.statusCode.toString())
                            it.put("info", "Something is wrong :C")
                        }
                    }
                }

            }catch(e: Exception){
                Log.e("processImage EXCEPTION", e.toString())
                return@withContext null
            }
        }
    }

    suspend fun getImage(guid: String, jwtToken: String ): File?{

        val endpoint = "/api/image/get/"

        return withContext(Dispatchers.IO){
            val tempFile = File.createTempFile("temp", ".tmp")

            val (request , response, result) =  Fuel.download(SERVER_URL + endpoint + guid,
                parameters = listOf("format" to "GraphML"))
                .fileDestination{
                        response, url -> tempFile
                }.progress { readBytes, totalBytes ->
                    val progress = readBytes.toFloat() / totalBytes.toFloat() * 100
                    //Log.i("progress", "Bytes downloaded $readBytes / $totalBytes ($progress %)")
                }.header(mapOf("authorization" to "Bearer $jwtToken"))
                .responseString()

            Log.i("getImage request", request.toString())
            Log.i("getImage response", response.toString())

            if (response.statusCode == 200){
                return@withContext tempFile
            }else{
                return@withContext null
            }
        }

    }
}