package com.pzpg.ogr

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.pzpg.ogr.api.RequestManager
import com.pzpg.ogr.api.request.RequestServer
import com.pzpg.ogr.api.request.TimeOutException
import kotlinx.android.synthetic.main.fragment_activity_sign_in.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignInFragmentActivity : FragmentActivity(){

    private val TAG = "SignInFragmentActivity"

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var myAccount: GoogleSignInAccount? = null

    lateinit var requestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity_sign_in)

        requestManager = RequestManager(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_FULL))
            .requestServerAuthCode(getString(R.string.WEB_ID))
            .requestIdToken(getString(R.string.WEB_ID))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val logo: Bitmap = BitmapFactory.decodeResource(
            resources,
            R.mipmap.logo_200x200
        )

        imageView3.setImageBitmap(logo)

        val action: String? = intent.getStringExtra(EXTRA_ACTION)

        action?.also {
            when(it){
                SIGN_IN -> {
                    signIn()
                    finish()
                }
                SIGN_OUT -> {
                    signOut()
                    finish()
                }
                SIGN_LAYOUT ->{

                }
            }
        }

    }

    fun clickSignIn(view: View){
        signIn()
    }

    fun clickGetToken(view: View){
        myAccount?.let {
            getToken(it)
        }
    }

    private fun signIn() {
        Log.d(TAG, "signIn")
        mGoogleSignInClient.signInIntent.also {
            startActivityForResult(it, REQUEST_SIGN_IN)
        }
    }

    private fun signOut(){
        Log.d(TAG, "signOut")
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void?>) {
                    myAccount = null
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("SIGN IN FRAGMENT", "onActivityResult")

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == REQUEST_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            myAccount = completedTask.getResult(ApiException::class.java)
            myAccount?.let {
                textView2.text = "Hello: ${it.displayName}"
                getToken(it)
            }
        } catch (e: ApiException) {
            textView_error.visibility = VISIBLE
            textView_error.text = "Failed google signIn: ${e.statusCode} "
            Log.w("Sign in", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun getToken(account: GoogleSignInAccount)
    {
        Log.i("getToken", "getToken IN")
        val currentActivity = this
        CoroutineScope(Dispatchers.Main).launch{
            try {
                requestManager.authenticate(account)
                finish()
            }catch (e: TimeOutException){
                textView_error.visibility = VISIBLE
                textView_error.text = "Problem with the server connecting..."
                button_reconect.visibility = VISIBLE
                sign_in_button.isEnabled = false
                Toast.makeText(currentActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object{
        const val EXTRA_ACTION = "EXTRA_ACTION"
        const val SIGN_IN = "sign_in"
        const val SIGN_OUT = "sign_out"
        const val SIGN_LAYOUT= "sign_layout"
    }

}