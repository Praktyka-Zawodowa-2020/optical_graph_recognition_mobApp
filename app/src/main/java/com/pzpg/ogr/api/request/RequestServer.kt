package com.pzpg.ogr.api.request

import android.util.Log
import com.goebl.david.Webb
import com.goebl.david.WebbException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class RequestServer{
    private val TAG = "RequestServer"
    private val SERVER_URL = "https://10.0.2.2:443"

    suspend fun authorize(account: GoogleSignInAccount?): JSONObject?{
        val endpoint = "/users/authenticate"
        Log.d(TAG, "start authorizeRequest")

        val msg = JSONObject()
        try {
            msg.accumulate("idToken", account?.idToken)
            msg.accumulate("authCode", account!!.serverAuthCode)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val webb: Webb = Webb.create()

        return withContext(Dispatchers.IO) {
            try{
                webb.post(SERVER_URL + endpoint)
                    .useCaches(false)
                    .body(msg)
                    .ensureSuccess()
                    .asJsonObject()
                    .body

            }catch (e: JSONException){
                return@withContext null
            }catch (e: WebbException){
                return@withContext null
            }


        }
    }

}