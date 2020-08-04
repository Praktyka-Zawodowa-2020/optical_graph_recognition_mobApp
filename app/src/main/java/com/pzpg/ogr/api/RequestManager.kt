package com.pzpg.ogr.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
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

class RequestManager(private val context: Context, private val account: GoogleSignInAccount) {
    private val tokenManager: TokenManager = TokenManager(context)
    private val urlServer = context.getString(R.string.url_server)

    /**
     * Suspend fun to called from a coroutine, required for user authentication to the server.
     *
     * @return[Boolean] indicates successful authentication on the server
     */
    suspend fun authenticate(): Boolean = withContext(Dispatchers.Main){

        try {
            val result = RequestServer(urlServer).authorize(account)
            tokenManager.setJwtToken(result.getString("jwtToken"))
            tokenManager.setRefreshToken(result.getString("refreshToken"))
            true
        }
        catch (e: BadRequestException){
            Log.e("BadRequestException", e.toString())
            Toast.makeText(context, "you need to re-login in the settings", Toast.LENGTH_LONG).show()
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
        Log.d("refresh", "start")

        val account = GoogleSignIn.getLastSignedInAccount(context) ?: return@withContext false

        var refreshToken = tokenManager.getRefreshToken()

        if (refreshToken == null){
            authenticate()
            refreshToken = tokenManager.getRefreshToken() ?: return@withContext false
        }

        try {
            val result = RequestServer(urlServer).refreshToken(refreshToken)
            tokenManager.setJwtToken(result.getString("jwtToken"))
            tokenManager.setRefreshToken(result.getString("refreshToken"))
            Log.d("refresh", "done")
            true
        }
        catch (e: BadRequestException){
            Log.d("refresh", "try authenticate")
            if(authenticate()){
                Log.d("refresh", "done")
                true
            }
            else{
                Log.e("BadRequestException", e.toString())
                false
            }
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

        if(!checkTokens())
            return@withContext null

        val jwtToken = tokenManager.getJwtToken()

        try {
            RequestServer(urlServer)
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
         mode: ProcessMode = ProcessMode.GRID_BG
    ): Boolean = withContext(Dispatchers.Main){

        if(!checkTokens())
            return@withContext false

        val jwtToken = tokenManager.getJwtToken()
        Log.i("processImage", "start")

        try {
            val guid = uploadImage(path, name) ?: return@withContext false
            RequestServer(urlServer)
                .processImage(guid, jwtToken!!, mode)
            true

        }catch (e: UnauthorizedException){
            val refreshed = refresh()
            if (refreshed) processImage(path, name, mode)
            else false
        }catch (e: RequestServerException){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            false
        }
    }

    suspend fun processImage(
         guid: String,
         mode: ProcessMode = ProcessMode.GRID_BG
    ): Boolean = withContext(Dispatchers.Main){

        val jwtToken = tokenManager.getJwtToken()

        Log.d("processImage GUID", guid)

        try {
            RequestServer(urlServer)
                .processImage(guid, jwtToken!!, mode)
            true
        }catch (e: UnauthorizedException){
            val refreshed = refresh()
            if (refreshed) processImage(guid, mode)
            else false
        }catch (e: RequestServerException){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            false
        }
    }

    suspend fun downloadProcessedGraph(
        guid: String,
        format: GraphFormat = GraphFormat.GraphML
    ): File? = withContext(Dispatchers.IO){
        val jwtToken = tokenManager.getJwtToken()

        try {
            RequestServer(urlServer).downloadGraph(guid, format, jwtToken!!)
        }catch (e: UnauthorizedException){
            val refreshed = refresh()
            if (refreshed) downloadProcessedGraph(guid, format)
            else null
        }catch (e: RequestServerException){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            null
        }
    }


    suspend fun getHistory(): JSONArray?{
        val jwtToken = tokenManager.getJwtToken()
        return try{
            RequestServer(urlServer)
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

    suspend fun deleteAll(): Boolean = withContext(Dispatchers.Main){
        val jwtToken = tokenManager.getJwtToken()
        try {
            RequestServer(urlServer).deleteAll(jwtToken!!)
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            true
        }catch (e: UnauthorizedException){
            val refreshed = refresh()
            if (refreshed) deleteAll()
            else false
        }catch (e: RequestServerException){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            false
        }
    }

    private suspend fun checkTokens(): Boolean = withContext(Dispatchers.Main){
        Log.d("checkTokens", "start")
        val jwtToken = tokenManager.getJwtToken()
        val refreshToken = tokenManager.getRefreshToken()
        if (jwtToken != null && refreshToken != null){
            Log.d("checkTokens", "ok")
            return@withContext true
        }else{
            Log.d("checkTokens", "no token, try refresh")
            refresh()
        }
    }

    suspend fun deleteData(guid: String): Boolean = withContext(Dispatchers.Main){

        val jwtToken = tokenManager.getJwtToken()
        try {
            RequestServer(urlServer).deleteData(guid,jwtToken!!)
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            true
        }catch (e: UnauthorizedException){
            val refreshed = refresh()
            if (refreshed) deleteData(guid)
            else false
        }catch (e: RequestServerException){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            false
        }
    }

}
