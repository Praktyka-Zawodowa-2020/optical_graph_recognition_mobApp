package com.pzpg.ogr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pzpg.ogr.api.RequestManager
import com.pzpg.ogr.graph.FruchtermanReingoldActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class ProcessActivity : AppCompatActivity() {
    private lateinit var photoPath: String
    private lateinit var account: GoogleSignInAccount
    private lateinit var requestManager : RequestManager
    private lateinit var graphFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process)

        supportActionBar?.title = "Process";

        photoPath = intent.getStringExtra("EXTRA_PHOTO_PATH")!!
        account = GoogleSignIn.getLastSignedInAccount(this)!!

        Log.i("ProcessActivity",photoPath)
        Log.i("ProcessActivity",account.toString() )

        requestManager = RequestManager(this)

    }

    private fun createImageFile(): File {
        // Create an image file name
        val storageDir: File? = getExternalFilesDir("graph")
        return File.createTempFile(
            "graph_", /* prefix */
            ".gml", /* suffix */
            storageDir /* directory */
        )
    }


    fun process(view: View){

        val splits = photoPath.split('/')
        val dir = splits.slice(0..(splits.size-2))
        val dirString = dir.joinToString(postfix = "/", separator = "/")

        val photoName = splits.takeLast(1)[0]

        val textInfo = findViewById<TextView>(R.id.textView_graphInfo)
        val processButton = findViewById<Button>(R.id.button_process)
        val openButton = findViewById<Button>(R.id.button_openGraph)

        val finishContainer = findViewById<ConstraintLayout>(R.id.finish_container)
        processButton.isEnabled = false

        CoroutineScope(Dispatchers.Main).launch {
            val guid = requestManager.processImage(dirString, photoName)
            textInfo.text = guid
            finishContainer.visibility = VISIBLE
            openButton.isEnabled = false
            if(guid != null){
                val file = requestManager.getImage(guid)
                if (file != null){
                    graphFile = createImageFile()
                    file.copyTo(graphFile, overwrite=true)
                    file.delete()
                }
            }else{
                Log.i("process", "guid = $guid")
            }

            openButton.isEnabled = true
        }
    }

    fun openGraph(view: View){
        Intent(this, FruchtermanReingoldActivity::class.java).also { graphActivity->
            graphActivity.putExtra("EXTRA_GRAPH_NAME", graphFile.name)
            graphActivity.putExtra("EXTRA_GRAPH_EXTENSION", "gml")
            startActivity(graphActivity)
        }
    }


}