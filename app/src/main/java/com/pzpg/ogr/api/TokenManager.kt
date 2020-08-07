package com.pzpg.ogr.api

import android.content.Context

/**
 * Class for managing the extraction of tokens from shared preferences.
 *
 * @property[sharedPref] shared preferences for token content
 *
 * @param[context] a context where class has created
 *
 * @author Władysław Jakołcewicz
 */
class TokenManager(context: Context) {

    private val sharedPref = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    /**
     * Set Jwt token in shared preferences.
     *
     * @param[jwtToken]
     */
    fun setJwtToken(jwtToken: String){
        with(sharedPref.edit()) {
            putString("jwtToken", jwtToken)
            commit()
        }
    }

    /**
     * Get Jwt token from shared preferences.
     *
     * @return[String] jwtToken
     */
    fun getJwtToken(): String? {
        return sharedPref.getString("jwtToken", null)
    }

    /**
     * Set Refresh token in shared preferences.
     *
     * @param[refreshToken]
     */
    fun setRefreshToken(refreshToken: String){
        with(sharedPref.edit()) {
            putString("refreshToken", refreshToken)
            commit()
        }
    }

    /**
     * Get Refresh token from shared preferences.
     *
     * @return[String] refreshToken
     */
    fun getRefreshToken(): String? {
        return sharedPref.getString("refreshToken", null)
    }

    /**
     * Clean our shared preferences
     */
    fun clear(){
        with(sharedPref.edit()) {
            putString("jwtToken", null)
            putString("refreshToken", null)
            commit()
        }
    }
}