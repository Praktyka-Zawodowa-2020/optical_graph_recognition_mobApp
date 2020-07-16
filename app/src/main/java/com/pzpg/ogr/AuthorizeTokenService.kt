package com.pzpg.ogr

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import java.util.*

class AuthorizeTokenService : Service() {
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    val duration = Toast.LENGTH_SHORT

    private var someIsChange: Boolean = true
    private var isAuthorizedLast: Boolean = false
    private var isAuthorized: Boolean = false

    private val TAG = "AuthorizeTokenService"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //return super.onStartCommand(intent, flags, startId)

        Toast.makeText(applicationContext, "Service started.", duration).show()

        // Do a periodic task
        mHandler = Handler()
        mRunnable = Runnable {
            checkToken()
        }
        mHandler.postDelayed(mRunnable, 5000)

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed.")
        mHandler.removeCallbacks(mRunnable)
    }

    private fun checkToken() {
        Log.d(TAG, "checkToken")
        mHandler.postDelayed(mRunnable, 5000)
    }
}
