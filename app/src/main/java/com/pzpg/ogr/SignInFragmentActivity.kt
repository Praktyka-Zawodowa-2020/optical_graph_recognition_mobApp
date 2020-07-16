package com.pzpg.ogr

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


class SignInFragmentActivity : FragmentActivity(), View.OnClickListener {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity_sign_in)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_FULL))
            .requestServerAuthCode(getString(R.string.WEB_ID))
            .requestIdToken(getString(R.string.WEB_ID))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val action: String? = intent.getStringExtra("EXTRA_ACTION")

        action?.also {
            when(it){
                "sign_in" -> {
                    signIn()
                    finish()
                }
                "sign_out" -> {

                    signOut()
                    finish()
                }
                "notify_sign_in" ->{

                }
            }
        }


        val buttonSignIn: SignInButton = findViewById(R.id.sign_in_button)
        val buttonBack: Button = findViewById(R.id.button_back)
        buttonSignIn.setOnClickListener(this)
        buttonBack.setOnClickListener(this)
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
        mGoogleSignInClient.signInIntent.also {
            startActivityForResult(it, RC_SIGN_IN)
        }
    }

    private fun signOut(){
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void?>) {
                    // ...
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("SIGN IN FRAGMENT", "onActivityResult")

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            Log.d("Sign in", "signInResult:succeeded")


            // Call Optical Graph Server Api and authorize it aswell
            try {
                AuthorizeOpticalGraphApi().execute(account)
            } catch (e: Exception) {
                Log.d("AUTHORIZE SERVER", e.toString())
            }

            updateUI(account!!)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Sign in", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.sign_in_button -> signIn()
            R.id.button_back -> finish()
        }
    }

}