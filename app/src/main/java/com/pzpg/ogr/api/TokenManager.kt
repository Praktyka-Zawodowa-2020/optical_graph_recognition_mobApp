package com.pzpg.ogr.api

import android.content.Context

class TokenManager(context: Context) {

    private val sharedPref = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    fun setJwtToken(jwtToken: String){
        with(sharedPref.edit()) {
            putString("jwtToken", jwtToken)
            commit()
        }
    }

    fun getJwtToken(): String? {
        return sharedPref.getString("jwtToken", null)
    }

    fun setRefreshToken(refreshToken: String){
        with(sharedPref.edit()) {
            putString("refreshToken", refreshToken)
            commit()
        }
    }

    fun getRefreshToken(): String? {
        return sharedPref.getString("refreshToken", null)
    }
}