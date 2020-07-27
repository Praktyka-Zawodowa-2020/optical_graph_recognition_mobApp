package com.pzpg.ogr.api

import android.content.Context
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pzpg.ogr.R
import com.pzpg.ogr.api.request.*
import org.json.JSONArray
import java.io.File

/**
 * Class for managing requests to the server.
 *
 * @property[context] a context where class has created
 * @author github.com/Graidaris
 */

class RequestManager(val context: Context) {
    private val tokenManager: TokenManager = TokenManager(context)

    /**
     * Suspend fun to called from a coroutine, required for user authentication to the server.
     *
     * @param[account] has GoogleSignInAccount type
     */
    suspend fun authenticate(account: GoogleSignInAccount){
        val urlServer = context.getString(R.string.url_server)
        try {
            val result = RequestServer(urlServer).authorize(account)
            tokenManager.setJwtToken(result.getString("jwtToken"))
            tokenManager.setRefreshToken(result.getString("refreshToken"))
        }catch (e: RequestServerException){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Suspend fun to called from a coroutine, required for refresh jwtToken
     *
     * @return Boolean which equal True (when the token has been refreshed) or False (when something went wrong)
     */
    suspend fun refresh(): Boolean{
        val urlServer = context.getString(R.string.url_server)
        val refreshToken = tokenManager.getRefreshToken()

        return try {
            val result = RequestServer(urlServer).refreshToken(refreshToken!!)
            tokenManager.setJwtToken(result.getString("jwtToken"))
            tokenManager.setRefreshToken(result.getString("refreshToken"))
            true
        } catch (e: RequestServerException){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            false
        }
    }

    /**
     * Suspend fun to called from a coroutine, required for refresh jwtToken
     *
     * @return guid: String of uploaded image or none, when something went wrong
     */
    suspend fun uploadImage(path: String, name: String): String?{
        val jwtToken = tokenManager.getJwtToken()
        return try {
            RequestServer(context.getString(R.string.url_server))
                .uploadImage(path, name, jwtToken!!)

        }catch (e: UnauthorizedException){
            val refreshed = refresh()
            if (refreshed) uploadImage(path, name)
            else null
        } catch (e: RequestServerException){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            null
        }
    }

    /**
     * Suspend fun to called from a coroutine, required for process uploaded image [uploadImage]
     *
     * @return temporary file with graph inside or none, when something went wrong
     */
    suspend fun processImage(
         path: String,
         name: String,
         mode: ProcessMode = ProcessMode.GRID_BG,
         format: GraphFormat = GraphFormat.GraphML
    ): File?{

        val jwtToken = tokenManager.getJwtToken()
        val guid = uploadImage(path, name) ?: return null
        return try {
            RequestServer(context.getString(R.string.url_server))
                .processImage(guid, jwtToken!!, mode, format)

        }catch (e: UnauthorizedException){
            val refreshed = refresh()
            if (refreshed) processImage(path, name, mode, format)
            else null
        }catch (e: RequestServerException){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            null
        }
    }

    suspend fun getHistory(): JSONArray?{
        val jwtToken = tokenManager.getJwtToken()
        return try{
            RequestServer(context.getString(R.string.url_server))
                .getHistory(jwtToken!!)
        }catch (e: UnauthorizedException){
            val refreshed = refresh()
            if (refreshed) getHistory()
            else null
        }catch (e: RequestServerException){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            null
        }
    }
}
