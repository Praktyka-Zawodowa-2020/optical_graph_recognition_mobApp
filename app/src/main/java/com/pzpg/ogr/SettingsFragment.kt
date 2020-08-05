package com.pzpg.ogr

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import com.pzpg.ogr.SignInFragmentActivity.Companion.EXTRA_ACTION
import com.pzpg.ogr.SignInFragmentActivity.Companion.EXTRA_SIGN_IN
import com.pzpg.ogr.SignInFragmentActivity.Companion.EXTRA_SIGN_OUT
import com.pzpg.ogr.api.RequestManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * Fragment responsible for some preferences
 */
class SettingsFragment : Fragment(), View.OnClickListener {

    private val TAG = "SettingsFragment"
    private var account: GoogleSignInAccount? = null
    private lateinit var requestManager: RequestManager
    private var picDir: File? = null
    private var graphDir: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Create the fragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val signOutButton = view.findViewById<Button>(R.id.button_signInOut)
        val cleanPic = view.findViewById<Button>(R.id.button_cleanPicture)
        val cleanDoc = view.findViewById<Button>(R.id.button_cleanDocs)
        val removeAll = view.findViewById<Button>(R.id.button_removeFromServer)
        signOutButton.setOnClickListener(this)
        cleanPic.setOnClickListener(this)
        cleanDoc.setOnClickListener(this)
        removeAll.setOnClickListener(this)

        picDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        graphDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        calculateLocalData(view)

        updateUI(view)
        return view
    }


    @SuppressLint("SetTextI18n")
    private fun calculateLocalData(view: View) {
        Log.i("calculateLocalData", "start")
        view.let { it ->
            val graphInfoSize = it.findViewById<TextView>(R.id.textView_DocSize)
            val picInfoSize = it.findViewById<TextView>(R.id.textView_PicSize)
            val picCount = it.findViewById<TextView>(R.id.textView_PicCount)
            val graphCount = it.findViewById<TextView>(R.id.textView_DocCount)

            CoroutineScope(Dispatchers.Main).launch {
                graphDir?.let { dir ->
                    val (size, count) = folderInfo(dir)
                    Log.i("Graph dir info", "Size=$size, Count=$count ")
                    graphInfoSize?.let { sizeInfo ->
                        var suf = "B"
                        var sizeConv: Float = size.toFloat()
                        if (size > 1000) {
                            sizeConv /= 1000.0f
                            suf = "KB"
                        }
                        if (size > 1000000) {
                            sizeConv /= 1000.0f
                            suf = "MB"
                        }

                        sizeInfo.text = "Size: $sizeConv $suf"
                    }

                    graphCount?.let { countInfo ->
                        countInfo.text = "Graphs: $count"
                    }
                }

                picDir?.let { dir ->
                    val (size, count) = folderInfo(dir)
                    Log.i("Pic dir info", "Size=$size, Count=$count ")
                    picInfoSize?.let { sizeInfo ->
                        var suf = "B"
                        var sizeConv: Float = size.toFloat()
                        if (size > 1000) {
                            sizeConv /= 1000.0f
                            suf = "KB"
                        }
                        if (size > 1000000) {
                            sizeConv /= 1000.0f
                            suf = "MB"
                        }

                        sizeInfo.text = "Size: $sizeConv $suf"
                    }

                    picCount?.let { countInfo ->
                        countInfo.text = "Pictures: $count"
                    }
                }
            }
        }
    }

    /**
     *  Get folder size and count of files inside
     *
     *  @param[directory]
     *  @return[Pair] (size, count)
     */
    private fun folderInfo(directory: File): Pair<Long, Int> {
        var length: Long = 0
        var count: Int = 0

        directory.listFiles()?.forEach { file ->
            if (file.isFile) {
                length += file.length()
                count += 1
            } else {
                val (size, retCount) = folderInfo(file)
                length += size
                count += retCount
            }
        }

        return Pair(length, count)
    }

    /**
     * Cleans the directory
     *
     * @param[dir]
     */
    private fun cleanDir(dir: File) {
        dir.let {
            it.listFiles()?.forEach { file ->
                if (file.isFile) file.delete()
                else if (file.isDirectory) cleanDir(file)
            }
        }
        calculateLocalData(requireView())
    }

    private fun updateUI(view: View) {
        val accountTextView = view.findViewById<TextView>(R.id.textView_infoAccount)
        val buttonSignInOut = view.findViewById<Button>(R.id.button_signInOut)
        val avatarView = view.findViewById<ImageView>(R.id.imageView4)

        if (account != null) {
            val avatarUri: Uri? = account?.photoUrl
            avatarUri?.let {
                Glide.with(this)
                    .load(it)
                    .into(avatarView)
            }

            accountTextView.text = account!!.displayName
            buttonSignInOut.text = "sign out"
        } else {
            try {
                avatarView.setImageResource(R.mipmap.empty_avatar)
            } catch (e: Resources.NotFoundException) {
                avatarView.setBackgroundColor(0)
            }

            accountTextView.text = "You are not sign in"
            buttonSignInOut.text = "sign in"
        }
    }

    private fun deleteServerData(){
        val currActivity = requireActivity()
        CoroutineScope(Dispatchers.Main).launch {
            account?.let { acc->
                RequestManager(currActivity, acc).deleteAll()
            }
        }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_signInOut -> {
                Intent(
                    requireActivity(),
                    SignInFragmentActivity::class.java
                ).also { signInActivity ->
                    if (account != null) {
                        signInActivity.putExtra(EXTRA_ACTION, EXTRA_SIGN_OUT)
                        startActivityForResult(
                            signInActivity,
                            REQUEST_SIGN_OUT_SETTING
                        )
                    } else {
                        signInActivity.putExtra(EXTRA_ACTION, EXTRA_SIGN_IN)
                        startActivityForResult(
                            signInActivity,
                            REQUEST_SIGN_IN_SETTING
                        )
                    }
                }
            }
            R.id.button_cleanDocs -> {
                graphDir?.let {
                    CoroutineScope(Dispatchers.Main).launch {
                        val cleanDoc = view.findViewById<Button>(R.id.button_cleanDocs)
                        cleanDoc.isEnabled = false
                        cleanDir(it)
                        cleanDoc.isEnabled = true
                    }
                }
            }
            R.id.button_cleanPicture -> {
                picDir?.let {
                    CoroutineScope(Dispatchers.Main).launch {
                        val cleanPic = view.findViewById<Button>(R.id.button_cleanPicture)
                        cleanPic.isEnabled = false
                        cleanDir(it)
                        cleanPic.isEnabled = true
                    }
                }
            }
            R.id.button_removeFromServer ->{
                deleteServerData()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_SIGN_OUT_SETTING -> view?.also {
                account = null
                updateUI(it)

                Intent(requireActivity(), SignInFragmentActivity::class.java).also { intentSignIn ->
                    startActivityForResult(
                        intentSignIn,
                        SIGN_OUT_ACTIVITY
                    )
                }
            }
            SIGN_OUT_ACTIVITY -> view?.also {
                account = GoogleSignIn.getLastSignedInAccount(requireActivity())
                if (account == null) {
                    requireActivity().finish()
                    updateUI(it)
                } else {
                    updateUI(it)
                }
            }
        }
    }

    companion object {
        const val REQUEST_SIGN_OUT_SETTING = 1
        const val REQUEST_SIGN_IN_SETTING = 2
        const val SIGN_OUT_ACTIVITY = 3
    }
}
