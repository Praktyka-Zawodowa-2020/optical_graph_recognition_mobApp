package com.pzpg.ogr.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pzpg.ogr.R
import com.pzpg.ogr.api.request.*
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File
import kotlinx.coroutines.Dispatchers

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
     * @return[Boolean] indicates successful authentication on the server
     */
    suspend fun authenticate(account: GoogleSignInAccount): Boolean = withContext(Dispatchers.Main){
        val urlServer = context.getString(R.string.url_server)
        try {
            val result = RequestServer(urlServer).authorize(account)
            tokenManager.setJwtToken(result.getString("jwtToken"))
            tokenManager.setRefreshToken(result.getString("refreshToken"))
            true
        }
        catch (e: BadRequestException){
            Log.e("BadRequestException", e.toString())
            false
        }
        catch (e: TimeOutException){
            Log.e("TimeOutException", e.toString())
            false
        }
        catch (e: RequestServerException){
            Log.e("RequestServerException", e.toString())
            false
        }
    }

    /**
     * Suspend fun to called from a coroutine, required for refresh jwtToken
     *
     * @return Boolean which equal True (when the token has been refreshed) or False (when something went wrong)
     */
    suspend fun refresh(): Boolean = withContext(Dispatchers.Main){
        val urlServer = context.getString(R.string.url_server)
        val refreshToken = tokenManager.getRefreshToken()
        try {
            val result = RequestServer(urlServer).refreshToken(refreshToken!!)
            tokenManager.setJwtToken(result.getString("jwtToken"))
            tokenManager.setRefreshToken(result.getString("refreshToken"))
            true
        }
        catch (e: BadRequestException){
            Log.e("BadRequestException", e.toString())
            false
        }
        catch (e: TimeOutException){
            Log.e("TimeOutException", e.toString())
            false
        }
        catch (e: RequestServerException){
            Log.e("RequestServerException", e.toString())
            false
        }
    }

    /**
     * Suspend fun to called from a coroutine, required for refresh jwtToken
     *
     * @return guid: String of uploaded image or none, when something went wrong
     */
    suspend fun uploadImage(path: String, name: String): String? = withContext(Dispatchers.Main){
        val jwtToken = tokenManager.getJwtToken()
        try {
            RequestServer(context.getString(R.string.url_server))
                .uploadImage(path, name, jwtToken!!)
        }
        catch (e: UnauthorizedException){
            val refreshed = refresh()
            if (refreshed) uploadImage(path, name)
            else null
        }
        catch (e: TimeOutException){
            Log.e("TimeOutException", e.toString())
            null
        }
        catch (e: RequestServerException){
            Log.e("RequestServerException", e.message.toString())
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
    ): File? = withContext(Dispatchers.Main){

        val jwtToken = tokenManager.getJwtToken()
        Log.i("processImage PATH", path)
        Log.i("processImage NAME", name)


        try {
            val guid = uploadImage(path, name) ?: return@withContext null
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

    suspend fun processImage(
         guid: String,
         mode: ProcessMode = ProcessMode.GRID_BG,
         format: GraphFormat = GraphFormat.GraphML
    ): File? = withContext(Dispatchers.Main){

        val jwtToken = tokenManager.getJwtToken()
        Log.i("processImage GUID", guid)

        try {
            RequestServer(context.getString(R.string.url_server))
                .processImage(guid, jwtToken!!, mode, format)

        }catch (e: UnauthorizedException){
            val refreshed = refresh()
            if (refreshed) processImage(guid, mode, format)
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
