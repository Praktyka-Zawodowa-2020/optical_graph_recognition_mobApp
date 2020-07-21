package com.pzpg.ogr.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.pzpg.ogr.R
import com.pzpg.ogr.api.request.RequestServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestsFragment : Fragment() {
    private val TAG = "TestsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_tests, container, false)
        val b_get_users = view.findViewById<Button>(R.id.button_Tget_users)

        b_get_users.setOnClickListener {
            getUsersRequest()
        }

        return view
    }

    private fun getUsersRequest(){

    }

}