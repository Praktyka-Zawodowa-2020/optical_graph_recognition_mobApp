package com.pzpg.ogr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pzpg.ogr.api.RequestManager
import com.pzpg.ogr.SignInFragmentActivity.Companion.EXTRA_ACTION
import com.pzpg.ogr.SignInFragmentActivity.Companion.EXTRA_SIGN_IN
import com.pzpg.ogr.SignInFragmentActivity.Companion.EXTRA_SIGN_OUT
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
        signOutButton.setOnClickListener(this)


        account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        updateUI(view)
        return view
    }

    private fun updateUI(view: View){
        val accountTextView = view.findViewById<TextView>(R.id.textView_infoAccount)
        val buttonSignInOut = view.findViewById<Button>(R.id.button_signInOut)
        val avatarView = view.findViewById<ImageView>(R.id.imageView4)

        if (account != null){
            val avatarUri: Uri? = account?.photoUrl
            avatarUri?.let {
                Glide.with(this)
                    .load(it)
                    .into(avatarView)
            }

            accountTextView.text = account!!.displayName
            buttonSignInOut.text = "sign out"
        }
        else{
            avatarView.setImageResource(R.mipmap.empty_avatar)
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



    override fun onClick(view: View) {
        when(view.id){
            R.id.button_signInOut -> {
                Intent(requireActivity(), SignInFragmentActivity::class.java).also { signInActivity ->
                    if (account != null){
                        signInActivity.putExtra(EXTRA_ACTION, EXTRA_SIGN_OUT)
                        startActivityForResult(signInActivity,
                            REQUEST_SIGN_OUT_SETTING
                        )
                    }else{
                        signInActivity.putExtra(EXTRA_ACTION, EXTRA_SIGN_IN)
                        startActivityForResult(signInActivity,
                            REQUEST_SIGN_IN_SETTING
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            REQUEST_SIGN_OUT_SETTING -> view?.also {
                account = null
                updateUI(it)

                Intent(requireActivity(), SignInFragmentActivity::class.java).also {intentSignIn ->
                    startActivityForResult(intentSignIn,
                        SIGN_OUT_ACTIVITY
                    )
                }
            }
            REQUEST_SIGN_IN_SETTING -> view?.also {
                account = GoogleSignIn.getLastSignedInAccount(requireActivity())
                getToken(account)

            }
            SIGN_OUT_ACTIVITY -> view?.also {
                account = GoogleSignIn.getLastSignedInAccount(requireActivity())
                if(account == null){
                    requireActivity().finish()
                    updateUI(it)
                }else{
                    updateUI(it)
                }
            }
        }
    }

    companion object{
        const val REQUEST_SIGN_OUT_SETTING = 1
        const val REQUEST_SIGN_IN_SETTING = 2
        const val SIGN_OUT_ACTIVITY = 3
    }
}
