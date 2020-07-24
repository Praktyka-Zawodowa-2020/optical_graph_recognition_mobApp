package com.pzpg.ogr.api

import android.content.Context
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pzpg.ogr.R
import com.pzpg.ogr.api.request.RequestServer
import java.io.File

class RequestManager(val context: Context) {

    suspend fun authenticate(account: GoogleSignInAccount){
        val sharedPref = context.getSharedPreferences(context.getString(R.string.user_preferences), Context.MODE_PRIVATE)
        val result = RequestServer(context.getString(R.string.url_server)).authorize(account)
        if (result != null) {
            if (result["code_response"] == "200") {
                with(sharedPref.edit()) {
                    putString(context.getString(R.string.jwtToken), result.get("jwtToken"))
                    putString(
                        context.getString(R.string.refreshToken),
                        result.get("refreshToken")
                    )
                    commit()
                }
            } else {
                Toast.makeText(
                    context,
                    "${result["code_response"]} - ${result["info"]}", Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(context, "authenticate exception", Toast.LENGTH_LONG).show()
        }
    }

    suspend fun refresh(){
        val sharedPref = context.getSharedPreferences(context.getString(R.string.user_preferences), Context.MODE_PRIVATE)
        val refreshToken = sharedPref.getString(context.getString(R.string.refreshToken), "null")
        val result = RequestServer(context.getString(R.string.url_server)).refreshToken(refreshToken!!)
        if (result != null){
            if (result["code_response"] == "200") {
                with(sharedPref.edit()){
                    putString(context.getString(R.string.jwtToken), result["jwtToken"])
                    putString(context.getString(R.string.refreshToken), result["refreshToken"])
                    commit()
                }
            }else if ( result["code_response"] == "400"){
                Toast.makeText(
                    context,
                    "Need sign in again", Toast.LENGTH_LONG
                ).show()
            } else{
                Toast.makeText(
                    context,
                    "${result["code_response"]} - ${result["info"]}", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    suspend fun uploadImage(path: String, name: String): String?{
        val sharedPref = context.getSharedPreferences(context.getString(R.string.user_preferences), Context.MODE_PRIVATE)
        val jwtToken = sharedPref.getString(context.getString(R.string.jwtToken), null)
        val result = RequestServer(context.getString(R.string.url_server))
            .uploadImage(path, name, jwtToken!!)

        if (result != null){
            if (result["code_response"] == "200") {
                return result["guid"]
            }else if(result["code_response"] == "401"){
                refresh()

                return uploadImage(path,name)

            }else {
                Toast.makeText(
                    context,
                    "uploadImage: ${result["code_response"]} - ${result["info"]}", Toast.LENGTH_LONG
                ).show()
            }
        }

        return null
    }

    suspend fun processImage(path: String, name: String): File?{
        val sharedPref = context.getSharedPreferences(context.getString(R.string.user_preferences), Context.MODE_PRIVATE)
        val jwtToken = sharedPref.getString(context.getString(R.string.jwtToken), null) ?: return null

        val guid = uploadImage(path, name) ?: return null

        val result = RequestServer(context.getString(R.string.url_server))
            .processImage(guid, jwtToken, null, null)

        return result
    }

    /*suspend fun processImage(path: String, name: String): String?{
        val sharedPref = context.getSharedPreferences(context.getString(R.string.user_preferences), Context.MODE_PRIVATE)
        val jwtToken = sharedPref.getString(context.getString(R.string.jwtToken), null)
        if (jwtToken == null){
            refresh()
            return processImage(path, name)
        }else{
            val result = RequestServer(context.getString(R.string.url_server))
                .processImage(path, name, jwtToken)

            if (result != null){
                when(result["code_response"]){
                    "200" -> {
                        return result["guid"]
                    }
                    "401" -> {
                    refresh()
                    return processImage(path,name)
                    }
                    else -> {
                    Toast.makeText(
                        context,
                        "${result["code_response"]} - ${result["info"]}", Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        return null
    }*/

    suspend fun getImage(guid: String): File?{
        val sharedPref = context.getSharedPreferences(context.getString(R.string.user_preferences), Context.MODE_PRIVATE)
        val jwtToken = sharedPref.getString(context.getString(R.string.jwtToken), null)

        return RequestServer(context.getString(R.string.url_server))
            .getImage(guid, jwtToken!!)
    }

    suspend fun uploadToGoogleDrive(){
        TODO("not implement")
    }

}