package com.pzpg.ogr.api.request

import android.os.AsyncTask
import android.util.Log
import com.goebl.david.Webb
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.json.JSONException
import org.json.JSONObject

class UsersRequest: AsyncTask<GoogleSignInAccount, Void, JSONObject>() {
    private val TAG = "UsersRequest"


    override fun doInBackground(vararg account: GoogleSignInAccount?): JSONObject? {
        Log.d(TAG, "doInBackground")

        val webb: Webb = Webb.create()

        val result = webb.post("https://10.0.2.2:443/users/authenticate")
            .useCaches(false)
            .ensureSuccess()
            .asJsonObject()
            .getBody()

        return result
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