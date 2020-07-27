package com.pzpg.ogr

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pzpg.ogr.api.RequestManager
import com.pzpg.ogr.api.request.ProcessMode
import com.pzpg.ogr.graph.FruchtermanReingoldActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ProcessActivity : AppCompatActivity() {
    private var photoPath: String? = null
    private var account: GoogleSignInAccount? = null
    private var requestManager : RequestManager? = null
    private var uriGraph: Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process)
        requestManager = RequestManager(this)
        supportActionBar?.title = "Process";
        val myAccount = GoogleSignIn.getLastSignedInAccount(this)
        val path = intent.getStringExtra("EXTRA_PHOTO_PATH")
        var fileToProcess: File? = null

        Log.i("ProcessActivity", path.toString())

        if(path != null && myAccount != null){

            val imageView: ImageView = findViewById(R.id.imageView2)
            val textViewName: TextView = findViewById(R.id.textView_photoName)

            fileToProcess = File(path)
            textViewName.text = fileToProcess.name
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

    private fun createGraphFile(): File {
        // Create an image file name
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        Log.i("createGraphFile", storageDir.toString())
        return File.createTempFile(
            "graph_",
            ".graphml",
            storageDir
        )
    }

    private fun getMode(): ProcessMode{
        val default = ProcessMode.GRID_BG
        val radioGroup : RadioGroup = findViewById(R.id.radioGroup)

        return when(radioGroup.checkedRadioButtonId){
            R.id.radioButton_auto -> default
            R.id.radioButton_clear -> ProcessMode.CLEAN_BG
            R.id.radioButton_grid -> ProcessMode.GRID_BG
            R.id.radioButton_printed -> ProcessMode.PRINTED
            else -> default
        }
    }


    fun process(view: View){

        val mode = getMode()

        val textInfo = findViewById<TextView>(R.id.textView_graphInfo)
        val processButton = findViewById<Button>(R.id.button_process)
        val openButton = findViewById<Button>(R.id.button_openGraph)

        val finishContainer = findViewById<ConstraintLayout>(R.id.finish_container)
        processButton.isEnabled = false
        val fileToProcess: File? = File(photoPath!!)

        val currentActivity = this

        CoroutineScope(Dispatchers.Main).launch {
            fileToProcess?.also {

                val graphTempFile = requestManager!!.processImage(
                    fileToProcess.parentFile!!.path,
                    fileToProcess.name,
                    mode
                )
                graphTempFile?.let { tempFile ->
                    val graphFile = createGraphFile()
                    uriGraph = FileProvider.getUriForFile(
                        currentActivity,
                        "com.pzpg.org.fileprovider",
                        graphFile
                    )

                    tempFile.copyTo(graphFile, overwrite=true)
                    tempFile.delete()
                }

                finishContainer.visibility = VISIBLE
                openButton.isEnabled = true
                processButton.isEnabled = true
                processButton.text = "reprocess"
            }
        }
    }

    fun share(view: View){
        val shareIntent = Intent(Intent.ACTION_SEND)
        val graphFile = File(uriGraph.toString())
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        shareIntent.putExtra(Intent.EXTRA_STREAM, uriGraph)
        shareIntent.type = "application/xml"
        shareIntent.flags = flags
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,graphFile.name);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Shared via OGR");

        val chooser = Intent.createChooser(shareIntent, null)

        val resInfoList: List<ResolveInfo> = packageManager
            .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName: String = resolveInfo.activityInfo.packageName
            grantUriPermission(packageName, uriGraph, flags)
        }

        startActivity(chooser)
    }

    fun openGraph(view: View){
        val uriFile = uriGraph
        uriFile?.let {
            Intent(this, FruchtermanReingoldActivity::class.java).also { graphActivity->
                graphActivity.data = uriFile
                startActivity(graphActivity)
            }
        }
    }
}