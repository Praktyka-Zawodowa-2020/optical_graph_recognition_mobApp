package com.pzpg.ogr.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pzpg.ogr.R
import com.pzpg.ogr.REQUEST_SIGN_IN_SETTING
import com.pzpg.ogr.REQUEST_SIGN_OUT_SETTING
import com.pzpg.ogr.api.RequestManager
import com.pzpg.ogr.authorize_google.EXTRA_ACTION
import com.pzpg.ogr.authorize_google.SIGN_IN
import com.pzpg.ogr.authorize_google.SIGN_OUT
import com.pzpg.ogr.authorize_google.SignInFragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsFragment : Fragment(), View.OnClickListener {

    private val TAG = "SettingsFragment"
    private var account: GoogleSignInAccount? = null
    private lateinit var requestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Create the fragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requestManager = RequestManager(requireContext())
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val signOutButton = view.findViewById<Button>(R.id.button_signInOut)
        val refreshTokenButton = view.findViewById<Button>(R.id.button_refreshToken)
        val processButton = view.findViewById<Button>(R.id.button_processImage)
        signOutButton.setOnClickListener(this)
        refreshTokenButton.setOnClickListener(this)
        processButton.setOnClickListener(this)


        account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        updateUI(view)
        return view
    }

    private fun updateUI(view: View){
        val accountTextView = view.findViewById<TextView>(R.id.textView_infoAccount)
        val buttonSignInOut = view.findViewById<Button>(R.id.button_signInOut)

        if (account != null){
            accountTextView.text = account!!.displayName
            buttonSignInOut.text = "sign out"
        }
        else{
            accountTextView.text = "You are not sign in"
            buttonSignInOut.text = "sign in"
        }
    }

    private fun getToken(account: GoogleSignInAccount?)
    {
        val button = requireView().findViewById<Button>(R.id.button_signInOut)

        if(account != null){
            button.isEnabled = false
            Log.i("getToken","CoroutineScope")
            CoroutineScope(Dispatchers.Main).launch {
                Log.i("CoroutineScope","start coroutine scope")
                requestManager.authenticate(account)
                Log.i("CoroutineScope","end coroutine scope")
                button.isEnabled = true
                updateUI(requireView())
            }
        }
    }

    private fun refreshToken(){
        val refreshTokenButton = requireView().findViewById<Button>(R.id.button_refreshToken)
        if (account != null){
            refreshTokenButton.isEnabled = false
            CoroutineScope(Dispatchers.Main).launch {
                requestManager.refresh()
                refreshTokenButton.isEnabled = true
            }
        }
    }

    private fun processImage(){
        val processButton = requireView().findViewById<Button>(R.id.button_processImage)
        val guidView = requireView().findViewById<TextView>(R.id.textView_image)
        if (account != null){
            processButton.isEnabled = false
            CoroutineScope(Dispatchers.Main).launch {
                val guid = requestManager.uploadImage(requireContext().getExternalFilesDir("Pictures")!!.absolutePath, "03.jpg")
                processButton.isEnabled = true
                guidView.text = guid
            }
        }
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.button_signInOut -> {
                Intent(requireActivity(), SignInFragmentActivity::class.java).also { signInActivity ->
                    if (account != null){
                        signInActivity.putExtra(EXTRA_ACTION, SIGN_OUT)
                        startActivityForResult(signInActivity, REQUEST_SIGN_OUT_SETTING)
                    }else{
                        signInActivity.putExtra(EXTRA_ACTION, SIGN_IN)
                        startActivityForResult(signInActivity, REQUEST_SIGN_IN_SETTING)
                    }
                }
            }
            R.id.button_refreshToken -> {
                refreshToken()
            }
            R.id.button_processImage -> {
                processImage()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            REQUEST_SIGN_OUT_SETTING -> view?.also {
                account = null
                updateUI(it)
            }
            REQUEST_SIGN_IN_SETTING -> view?.also {
                account = GoogleSignIn.getLastSignedInAccount(requireActivity())
                getToken(account)

            }
        }
    }
}
