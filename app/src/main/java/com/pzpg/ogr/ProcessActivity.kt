package com.pzpg.ogr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pzpg.ogr.api.RequestManager
import com.pzpg.ogr.graph.FruchtermanReingoldActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ProcessActivity : AppCompatActivity() {
    private var photoPath: String? = null
    private var account: GoogleSignInAccount? = null
    private var requestManager : RequestManager? = null
    private var graphFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process)

        supportActionBar?.title = "Process";
        val myAccount = GoogleSignIn.getLastSignedInAccount(this)
        val path = intent.getStringExtra("EXTRA_PHOTO_PATH")
        var fileToProcess: File? = null

        if(path != null && myAccount != null){

            val imageView: ImageView = findViewById(R.id.imageView2)
            val textViewNmae: TextView = findViewById(R.id.textView_photoName)

            fileToProcess = File(path)
            textViewNmae.text = fileToProcess.name
            photoPath = path
            requestManager = RequestManager(this)
            account = myAccount

            Glide.with(this)
                .load(path)
                .into(imageView)

        }else{
            Toast.makeText(this, "Need a path to file", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val storageDir: File? = getExternalFilesDir("graph")
        return File.createTempFile(
            "graph_",
            ".gml",
            storageDir
        )
    }


    fun process(view: View){

        /*val splits = photoPath.split('/')
        val dir = splits.slice(0..(splits.size-2))
        val dirString = dir.joinToString(postfix = "/", separator = "/")

        val photoName = splits.takeLast(1)[0]*/

        val textInfo = findViewById<TextView>(R.id.textView_graphInfo)
        val processButton = findViewById<Button>(R.id.button_process)
        val openButton = findViewById<Button>(R.id.button_openGraph)

        val finishContainer = findViewById<ConstraintLayout>(R.id.finish_container)
        processButton.isEnabled = false
        val fileToProcess: File? = File(photoPath!!)


        CoroutineScope(Dispatchers.Main).launch {
            fileToProcess?.also {
                val guid = requestManager!!.processImage(fileToProcess.path, fileToProcess.name)
                textInfo.text = guid
                finishContainer.visibility = VISIBLE
                openButton.isEnabled = false
                if(guid != null){
                    val file = requestManager!!.getImage(guid)
                    if (file != null){
                        graphFile = createImageFile()
                        file.copyTo(graphFile!!, overwrite=true)
                        file.delete()
                    }
                }else{
                    Log.i("process", "guid = $guid")
                }

                openButton.isEnabled = true
            }
        }
    }

    fun openGraph(view: View){
        Intent(this, FruchtermanReingoldActivity::class.java).also { graphActivity->
            graphActivity.putExtra("EXTRA_GRAPH_NAME", graphFile!!.name)
            graphActivity.putExtra("EXTRA_GRAPH_EXTENSION", "gml")
            startActivity(graphActivity)
        }
    }


}