package com.pzpg.ogr.authorize_google

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.pzpg.ogr.R
import com.pzpg.ogr.REQUEST_SIGN_IN
import com.pzpg.ogr.api.request.RequestServer
import kotlinx.coroutines.*


class SignInFragmentActivity : FragmentActivity(), View.OnClickListener {

    private val TAG = "SignInFragmentActivity"

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var myAccount: GoogleSignInAccount? = null
    private val RC_SIGN_IN = 0

    val uiScope = CoroutineScope(Dispatchers.Main)

    lateinit var requestServerServer: RequestServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity_sign_in)

        requestServerServer = RequestServer()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_FULL))
            .requestServerAuthCode(getString(R.string.WEB_ID))
            .requestIdToken(getString(R.string.WEB_ID))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

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


        val buttonSignIn: SignInButton = findViewById(R.id.sign_in_button)
        val buttonBack: Button = findViewById(R.id.button_back)
        val buttonSignOut: Button = findViewById(R.id.button_signInOut)
        buttonSignIn.setOnClickListener(this)
        buttonBack.setOnClickListener(this)
        buttonSignOut.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
    }


    private fun updateUI(account: GoogleSignInAccount?){
        val signInTextInfo = findViewById<TextView>(R.id.signIn_info)
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        val signOutButton = findViewById<Button>(R.id.button_signInOut)

        if (account != null){
            signInTextInfo.text = account.displayName
            signInButton.visibility = GONE
            signOutButton.visibility = VISIBLE
        }else{
            signInTextInfo.text = "You are not actually logIn"
            signInButton.visibility = VISIBLE
            signOutButton.visibility = GONE
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
            // Signed in successfully, show authenticated UI.
            Log.d("Sign in", "signInResult:succeeded")
            updateUI(myAccount)
            getToken(myAccount)

            // Call Optical Graph Server Api and authorize it aswell
           //AuthorizeRequest().execute(account)
            //val res = async{} requestServerServer.authorize(account)


        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Sign in", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun getToken(account: GoogleSignInAccount?)
    {
        CoroutineScope(Dispatchers.IO).launch{
            val result = requestServerServer.authorize(account)
            Log.d("TOKEN", result.toString())
        }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.sign_in_button -> {
                signIn()
                updateUI(myAccount)
            }
            R.id.button_back -> finish()
            R.id.button_signInOut -> {
                signOut()
                updateUI(null)
            }
        }
    }

}