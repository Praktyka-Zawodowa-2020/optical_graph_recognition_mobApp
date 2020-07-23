package com.pzpg.ogr

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
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
        requestManager = RequestManager(this)
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

        val textInfo = findViewById<TextView>(R.id.textView_graphInfo)
        val processButton = findViewById<Button>(R.id.button_process)
        val openButton = findViewById<Button>(R.id.button_openGraph)

        val finishContainer = findViewById<ConstraintLayout>(R.id.finish_container)
        processButton.isEnabled = false
        val fileToProcess: File? = File(photoPath!!)

        CoroutineScope(Dispatchers.Main).launch {
            fileToProcess?.also {

                Log.i("process",fileToProcess.parentFile.absolutePath )
                Log.i("process",fileToProcess.name )
                Log.i("process",requestManager.toString() )

                val dir = fileToProcess.parentFile?.absolutePath

                val guid = requestManager?.processImage(dir!!, fileToProcess.name)
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

    fun share(view: View){
        val uriFile = Uri.fromFile(graphFile)
        val shareIntent = Intent(Intent.ACTION_SEND)


        shareIntent.putExtra(Intent.EXTRA_STREAM,uriFile)
        shareIntent.type = "application/xml"
        shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,graphFile!!.name);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Shared via app");

        val resInfoList: List<ResolveInfo> = packageManager
            .queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName: String = resolveInfo.activityInfo.packageName
            grantUriPermission(packageName, uriFile,  Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }


        startActivity(Intent.createChooser(shareIntent, null))
    }

    fun openGraph(view: View){
        val uriFile = Uri.fromFile(graphFile)
        uriFile?.let {
            Intent(this, FruchtermanReingoldActivity::class.java).also { graphActivity->
                graphActivity.data = uriFile
                startActivity(graphActivity)
            }
        }
    }
}