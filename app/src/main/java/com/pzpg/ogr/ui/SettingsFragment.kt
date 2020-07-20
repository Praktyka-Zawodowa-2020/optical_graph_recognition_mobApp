package com.pzpg.ogr.ui

import android.content.Context
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
import com.pzpg.ogr.api.request.RequestServer
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Create the fragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val signOutButton = view.findViewById<Button>(R.id.button_signInOut)
        val refreshTokenButton = view.findViewById<Button>(R.id.button_refreshToken)
        signOutButton.setOnClickListener(this)
        refreshTokenButton.setOnClickListener(this)



        updateUI(view)
        return view
    }

    private fun updateUI(view: View){
        val accountTextView = view.findViewById<TextView>(R.id.textView_infoAccount)
        val buttonSignInOut = view.findViewById<Button>(R.id.button_signInOut)
        account = GoogleSignIn.getLastSignedInAccount(requireActivity())
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
        val sharedPref = requireActivity().getSharedPreferences(getString(R.string.user_preferences), Context.MODE_PRIVATE)
        CoroutineScope(Dispatchers.Main).launch{

            val result = RequestServer(getString(R.string.url_server)).authorize(account)
            with(sharedPref.edit()){
                putString(getString(R.string.jwtToken), result?.get("jwtToken"))
                putString(getString(R.string.refreshToken), result?.get("refreshToken"))
                commit()
            }

        }
    }

    private fun refreshToken(){
        val sharedPref = requireActivity().getSharedPreferences(getString(R.string.user_preferences), Context.MODE_PRIVATE)
        val jwtToken = sharedPref!!.getString(getString(R.string.jwtToken), null)
        val refreshToken = sharedPref!!.getString(getString(R.string.refreshToken), null)

        CoroutineScope(Dispatchers.Main).launch{

            val result = RequestServer(getString(R.string.url_server)).refreshToken(refreshToken!!, jwtToken!!)

        }
    }

    override fun onClick(view: View) {
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
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
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            REQUEST_SIGN_OUT_SETTING -> view?.also {
                updateUI(it)
            }
            REQUEST_SIGN_IN_SETTING -> view?.also {
                updateUI(it)
                getToken(account)

            }
        }
    }
}
