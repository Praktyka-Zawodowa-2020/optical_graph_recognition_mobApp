package com.pzpg.ogr.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.FileProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.pzpg.ogr.R
import com.pzpg.ogr.api.RequestManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestsFragment : Fragment() {
    private val TAG = "TestsFragment"
    private var requestManager:RequestManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestManager = RequestManager(requireContext())

        val view = inflater.inflate(R.layout.fragment_tests, container, false)
        val authorizeButton = view.findViewById<Button>(R.id.button_authorize)
        val refreshButton = view.findViewById<Button>(R.id.button_refresh)
        val uploadButton = view.findViewById<Button>(R.id.button_upload)
        val processButton = view.findViewById<Button>(R.id.button_process)

        authorizeButton.setOnClickListener {
            authorize()
        }
        refreshButton.setOnClickListener {
            refresh()
        }
        uploadButton.setOnClickListener {
            upload()
        }
        processButton.setOnClickListener {
            process()
        }

        return view
    }

    private fun authorize(){
        val account =  GoogleSignIn.getLastSignedInAccount(requireActivity())
        requestManager?.let{rm ->
            CoroutineScope(Dispatchers.IO).launch{
                if (account != null) {
                    rm.authenticate(account)
                }
            }
        }
    }
    private fun refresh(){
        requestManager?.let{rm ->
            CoroutineScope(Dispatchers.IO).launch{
                rm.refresh()
            }
        }
    }

    private fun upload(){
        val dir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        requestManager?.let{rm ->
            CoroutineScope(Dispatchers.IO).launch{
                rm.uploadImage(dir!!.absolutePath, "gallery_2175510765173524935.jpg")
            }
        }
    }
    private fun process(){
        requestManager?.let{rm ->
            CoroutineScope(Dispatchers.IO).launch{
                rm.refresh()
            }
        }
    }
}
