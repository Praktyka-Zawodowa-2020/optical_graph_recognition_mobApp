package com.pzpg.ogr.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.pzpg.ogr.R
import com.pzpg.ogr.REQUEST_SIGN_IN_SETTING
import com.pzpg.ogr.REQUEST_SIGN_OUT_SETTING
import com.pzpg.ogr.SignInFragmentActivity

class SettingsFragment : Fragment(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("FRAGMENT SETTINGS", "Create the fragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val signOutButton = view.findViewById<Button>(R.id.button_signInOut)
        signOutButton.setOnClickListener(this)

        updateUI(view)
        return view
    }

    private fun updateUI(view: View){
        val accountTextView = view.findViewById<TextView>(R.id.textView_infoAccount)
        val buttonSignInOut = view.findViewById<Button>(R.id.button_signInOut)
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if (account != null){
            accountTextView.text = account.displayName
            buttonSignInOut.text = "sign out"
        }
        else{
            accountTextView.text = "You are not sign in"
            buttonSignInOut.text = "sign in"
        }
    }

    override fun onClick(view: View) {

        if (view.id == R.id.button_signInOut){
            Intent(requireActivity(), SignInFragmentActivity::class.java).also { signInActivity ->
                val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
                if (account != null){
                    signInActivity.putExtra("EXTRA_ACTION", "sign_out")
                    startActivityForResult(signInActivity, REQUEST_SIGN_OUT_SETTING)
                }else{
                    signInActivity.putExtra("EXTRA_ACTION", "sign_in")
                    startActivityForResult(signInActivity, REQUEST_SIGN_IN_SETTING)
                }

            }
        }

        getView()?.also {
            updateUI(it)
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
            }
        }
    }
}
