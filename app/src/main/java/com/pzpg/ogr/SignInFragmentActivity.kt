package com.pzpg.ogr

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
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

/**
 * Fragment activity responsible for the authorization
 */
class SignInFragmentActivity : FragmentActivity(){

    private val TAG = "SignInFragmentActivity"

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var myAccount: GoogleSignInAccount? = null

    lateinit var requestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity_sign_in)

        //Google authorization
        //more info here https://developers.google.com/identity/sign-in/android/start-integrating
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(getString(R.string.WEB_ID))
            .requestIdToken(getString(R.string.WEB_ID))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        myAccount = GoogleSignIn.getLastSignedInAccount(this)

        val action: String? = intent.getStringExtra(EXTRA_ACTION)

        action?.also {
            when(it){
                EXTRA_SIGN_IN -> {
                    signIn()
                    finish()
                }
                EXTRA_SIGN_OUT -> {
                    signOut()
                    finish()
                }
                EXTRA_SIGN_LAYOUT ->{

                }
            }
        }

    }

    fun clickSignIn(view: View){
        signIn()
    }

    fun clickGetToken(view: View){
        myAccount?.let {
            serverAuthorize()
        }
    }

    private fun signIn() {
        Log.d(TAG, "signIn")
        mGoogleSignInClient.signInIntent.also {
            startActivityForResult(it, SIGN_IN)
        }
    }

    private fun signOut(){
        Log.d(TAG, "signOut")
        val currentActivity = this
       myAccount?.let {
           requestManager = RequestManager(this, it)
           requestManager.let {
               CoroutineScope(Dispatchers.Main).launch{
                   requestManager.revokeToken()

                   mGoogleSignInClient.signOut()
                       .addOnCompleteListener(currentActivity, object : OnCompleteListener<Void> {
                           override fun onComplete(task: Task<Void?>) {
                               myAccount = null
                           }
                       })
               }
           }
       }
    }

    @SuppressLint("SetTextI18n")
    private fun showInfo(message: String){
        val pref = "Info: "
        textView_info.text = pref + message
        textView_info.visibility = VISIBLE
    }

    private fun hideInfo(){
        textView_info.visibility = INVISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun showError(message: String){
        val pref = "Error: "
        textView_error.text = pref + message
        textView_error.visibility = VISIBLE
    }

    private fun hideError(){
        textView_error.visibility = INVISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        hideError()
        hideInfo()

        Log.d("SIGN IN FRAGMENT", "onActivityResult")

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        sign_in_button.isEnabled = false
        try {
            myAccount = completedTask.getResult(ApiException::class.java)
            myAccount?.let {
                requestManager = RequestManager(this, it)
                textView2.text = "Hello: ${it.displayName}"
                serverAuthorize()
            }
        } catch (e: ApiException) {
            sign_in_button.isEnabled = true
            showError("Failed google signIn: ${e.statusCode}")
            Log.w("Sign in", "signInResult:failed code=" + e.statusCode)
        }
    }



    private fun serverAuthorize()
    {
        Log.i("serverAuthorize", "serverAuthorize IN")
        showInfo("connecting to server...")
        hideError()
        button_reconect.isEnabled = false
        val currentActivity = this
        CoroutineScope(Dispatchers.Main).launch{

            if(requestManager.authenticate()){
                finish()
            }else {
                val message = "Problem with the server connecting..."
                showError(message)
                button_reconect.visibility = VISIBLE
                sign_in_button.isEnabled = false
                Toast.makeText(currentActivity, message, Toast.LENGTH_LONG).show()
            }
            hideInfo()
            button_reconect.isEnabled = true
        }
    }


    companion object{
        const val SIGN_IN = 1
        const val SIGN_OUT = 2

        const val EXTRA_ACTION = "EXTRA_ACTION"
        const val EXTRA_SIGN_IN = "sign_in"
        const val EXTRA_SIGN_OUT = "sign_out"
        const val EXTRA_SIGN_LAYOUT= "sign_layout"
    }

}