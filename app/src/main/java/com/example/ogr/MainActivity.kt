package com.example.ogr

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_TAKE_PHOTO = 1
    val EXTRA_BITMAP = "com.example.ogr.BITMAP"
    private var iamgeBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun editPhoto(view: View){

        if (iamgeBitmap != null){
            val intentEdit = Intent(this, EditActivity::class.java).apply {
                putExtra(EXTRA_BITMAP, iamgeBitmap as Bitmap)
            }
            startActivity(intentEdit)
        }
    }

    fun takePhoto(view: View){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {

                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.d("ERROR FILE CREATE", "exception with image creating")
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.org.fileprovider",
                        it
                    )
                    print(photoURI)
                    Log.d("PHOTO URI", photoURI.toString())

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                    //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI) fix this line

                }
            }
        }
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_test_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            /*if (imageBitmap != null){
                this.iamgeBitmap = imageBitmap
                imageView.setImageBitmap(imageBitmap)
            }*/
        }
    }
}