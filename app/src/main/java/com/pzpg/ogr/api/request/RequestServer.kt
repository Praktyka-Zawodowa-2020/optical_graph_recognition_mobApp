package com.pzpg.ogr.api.request

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class RequestServer(url_server: String){
    private val TAG = "RequestServer"
    private val SERVER_URL = url_server

    suspend fun authorize(account: GoogleSignInAccount?): MutableMap<String, String?>?{
        val endpoint = "/users/authenticate"
        Log.d(TAG, "start authorizeRequest")

        val msg = JSONObject()
        try {
            msg.accumulate("idToken", account?.idToken)
            msg.accumulate("authCode", account!!.serverAuthCode)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return withContext(Dispatchers.IO) {
            try{

                var jwtToken: String? = null
                var refreshToken: String? = null

                val (_, response, result) = Fuel.post(SERVER_URL + endpoint)
                    .header(Headers.CONTENT_TYPE to "application/json")
                    .body(msg.toString())
                    .responseString(Charsets.UTF_8)



                /*refreshToken = response.get("Set-Cookie").toString().split(';')[0].split('=')[1]
                jwtToken = JSONObject(result.get()).getString("jwtToken").toString()

                val res = mutableMapOf<String, String?>().also {
                    it["jwtToken"] = jwtToken
                    it["refreshToken"] = refreshToken
                }*/

                refreshToken = response.header("Set-Cookie").toString()
                Log.i("DECODE", refreshToken )
                //return@withContext  res

                return@withContext null

            }catch (e: Exception){
                Log.e("authenticate", e.toString())
                return@withContext null
            }
        }
    }

    suspend fun refreshToken(rToken: String, jwtToken: String): MutableMap<String, String?>?{
        val endpoint = "/users/refresh-token"


        return withContext(Dispatchers.IO) {
            val result = Fuel.post(SERVER_URL + endpoint)
                .header(Headers.SET_COOKIE to "refresh_token $rToken")
                .header(Headers.COOKIE to "refresh_token $rToken")
                .responseString()

            Log.i("refreshToken", result.toString())


            null
            }
        }


    suspend fun getUsers(jwtToken: String): JSONArray? {
        val endpoint = "/users/"

        return withContext(Dispatchers.IO) {
            try{

                val (_, response, result) = Fuel.get(SERVER_URL + endpoint)
                    .header(mapOf("authorization" to "bearer $jwtToken"))
                    .responseString()

                null
            }catch (e: Exception){
                Log.e("getUsers", e.toString())
                return@withContext null
            }
        }
    }



}