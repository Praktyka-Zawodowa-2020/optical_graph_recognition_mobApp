package com.pzpg.ogr

import android.os.AsyncTask
import android.util.Log
import com.goebl.david.Webb

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.json.JSONException
import org.json.JSONObject

class AuthorizeOpticalGraphApi : AsyncTask<GoogleSignInAccount, Void, JSONObject>() {



    override fun doInBackground(vararg account: GoogleSignInAccount?): JSONObject {
        Log.d("API", "doInBackground")
        val msg = JSONObject()
        try {
            msg.accumulate("idToken", account[0]?.idToken)
            msg.accumulate("authCode", account[0]!!.serverAuthCode)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val webb: Webb = Webb.create()

        return webb.post("https://10.0.2.2:443/users/authenticate")
            .useCaches(false)
            .body(msg)
            .ensureSuccess()
            .asJsonObject()
            .getBody()
    }

    override fun onPostExecute(result: JSONObject) {
        // TODO: check this.exception
        // TODO: do something with the feed
        Log.d("API", "onPostExecute")
        try {
            //activity.textView.setText(result.getString("jwtToken"))
            Log.d("API", result.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}