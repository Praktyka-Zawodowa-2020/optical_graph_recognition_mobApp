package com.pzpg.ogr

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.airbnb.paris.extensions.style
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.pzpg.ogr.api.RequestManager
import com.pzpg.ogr.api.request.GraphFormat
import com.pzpg.ogr.api.request.ProcessMode
import com.pzpg.ogr.graph.FruchtermanReingoldActivity
import kotlinx.android.synthetic.main.activity_process.*
import kotlinx.android.synthetic.main.fragment_activity_sign_in.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class ProcessActivity : AppCompatActivity() {
    private var photoPath: String? = null
    private var account: GoogleSignInAccount? = null
    private lateinit var requestManager: RequestManager
    private var uriGraphSix: Uri? = null
    private var uriGraphMl: Uri? = null
    private var guid: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process)

        supportActionBar?.title = "Process";
        val myAccount = GoogleSignIn.getLastSignedInAccount(this)
        val path = intent.getStringExtra("EXTRA_PHOTO_PATH")
        var fileToProcess: File? = null

        Log.i("ProcessActivity", path.toString())

        if (path != null && myAccount != null) {
            requestManager = RequestManager(this, myAccount)
            val imageView: ImageView = findViewById(R.id.imageView2)
            val textViewName: TextView = findViewById(R.id.textView_photoName)

            fileToProcess = File(path)
            textViewName.text = fileToProcess.name
            photoPath = path

            account = myAccount

            Glide.with(this)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView)

        } else {
            Toast.makeText(this, "Need a path to file or google signIn", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun createGraphFile(graphFormat: GraphFormat): File {
        // Create an image file name
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        Log.i("createGraphFile", storageDir.toString())
        return File.createTempFile(
            "graph_",
            graphFormat.suffix,
            storageDir
        )
    }

    private fun getMode(): ProcessMode {
        val default = ProcessMode.GRID_BG

        return when (modeRadioGroup.checkedRadioButtonId) {
            R.id.radioButton_auto -> default
            R.id.radioButton_clear -> ProcessMode.CLEAN_BG
            R.id.radioButton_grid -> ProcessMode.GRID_BG
            R.id.radioButton_printed -> ProcessMode.PRINTED
            else -> default
        }
    }

    fun clickShare(view: View) {
        when (shareRadioGroup.checkedRadioButtonId) {
            R.id.radioButton_FGML -> shareMl()
            R.id.radioButton_FG6 -> shareSix()
            R.id.radioButton_CFG6 -> shareContentSix()
        }
    }

    private fun getUriForTempFile(file: File, graphFormat: GraphFormat): Uri {
        val graphFile = createGraphFile(graphFormat)
        val uri = FileProvider.getUriForFile(
            this,
            "com.pzpg.org.fileprovider",
            graphFile
        )

        file.copyTo(graphFile, overwrite = true)
        file.delete()
        return uri
    }


    fun process(view: View) {
        val mode = getMode()

        val fileToProcess: File? = File(photoPath!!)


        textView_process.style(R.style.LoadingInfo)
        textView_downloadGml.style(R.style.LoadingInfo)
        textView_downloadG6.style(R.style.LoadingInfo)

        button_process.isEnabled = false

        CoroutineScope(Dispatchers.Main).launch {
            fileToProcess?.also {
                Log.i("process", "process image: $fileToProcess")

                if (guid == null){
                    textView_uploadImage.style(R.style.LoadingInfo)
                    guid = requestManager.uploadImage(
                        fileToProcess.parentFile!!.path,
                        fileToProcess.name
                    )
                }


                if (guid != null) {
                    textView_uploadImage.style(R.style.DoneInfo)

                    if (requestManager.processImage(guid!!, mode)){
                        textView_process.style(R.style.DoneInfo)
                        val graphTempFileGraphML =
                            requestManager.downloadProcessedGraph(guid!!, GraphFormat.GraphML)

                        if(graphTempFileGraphML != null){
                            uriGraphMl = getUriForTempFile(graphTempFileGraphML, GraphFormat.GraphML)
                            textView_downloadGml.style(R.style.DoneInfo)
                        }else{
                            textView_downloadGml.style(R.style.ErrorInfo)
                        }


                        val graphTempFileGSix =
                            requestManager.downloadProcessedGraph(guid!!, GraphFormat.Graph6)

                        if (graphTempFileGSix != null){
                            uriGraphSix = getUriForTempFile(graphTempFileGSix, GraphFormat.Graph6)
                            textView_downloadG6.style(R.style.DoneInfo)
                        }else{
                            textView_downloadG6.style(R.style.ErrorInfo)
                        }
                    }else{
                        textView_process.style(R.style.ErrorInfo)
                    }

                }else{
                    textView_uploadImage.style(R.style.ErrorInfo)
                }
            }

            button_process.isEnabled = true
        }
    }

    private fun shareMl() {
        uriGraphMl?.let { shareFile(it) }
    }

    private fun shareSix() {
        uriGraphSix?.let { shareFile(it) }
    }

    private fun shareContentSix() {
        uriGraphSix?.let { shareText(it) }
    }

    private fun shareText(uri: Uri) {
        Intent(Intent.ACTION_SEND).let { shareText ->
            val content =
                contentResolver.openInputStream(uri)?.readBytes()?.toString(Charsets.UTF_8)
            Log.d("shareText", content.toString())
            shareText.type = "text/plain"
            shareText.putExtra(Intent.EXTRA_TEXT, content);
            startActivity(Intent.createChooser(shareText, null))
        }
    }

    private fun shareFile(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        val graphFile = File(uri.toString())
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "text/xml"
        shareIntent.flags = flags
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, graphFile.name);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Shared via OGR");

        val chooser = Intent.createChooser(shareIntent, null)

        val resInfoList: List<ResolveInfo> = packageManager
            .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName: String = resolveInfo.activityInfo.packageName
            grantUriPermission(packageName, uri, flags)
        }
        startActivity(chooser)
    }

    fun openGraph(view: View) {
        val uriFile = uriGraphMl
        uriFile?.let {
            Intent(this, FruchtermanReingoldActivity::class.java).also { graphActivity ->
                graphActivity.data = uriFile
                startActivity(graphActivity)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showInfo(message: String) {
        val pref = "Info: "
        val textViewInfo = findViewById<TextView>(R.id.textView_info)
        textViewInfo.text = pref + message
        textViewInfo.visibility = VISIBLE
    }

    private fun hideInfo() {
        val pref = "Info: "
        val textViewInfo = findViewById<TextView>(R.id.textView_info)
        textViewInfo.text = pref
        textViewInfo.visibility = GONE
    }


}