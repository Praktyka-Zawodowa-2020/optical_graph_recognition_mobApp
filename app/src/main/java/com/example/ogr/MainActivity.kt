package com.example.ogr

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


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
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
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
            val pthFile = saveImage(imageBitmap)
            Log.d("FILE", pthFile.toString() + " is saved")

            /*if (imageBitmap != null){
                this.iamgeBitmap = imageBitmap
                imageView.setImageBitmap(imageBitmap)
            }*/
        }
    }


    private fun saveImage(image: Bitmap): String? {

        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            // Error occurred while creating the File
            Log.d("ERROR FILE CREATE", "exception with image creating")
            null
        }

        if (photoFile != null) {
            try {
                val fOut: OutputStream = FileOutputStream(photoFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return photoFile?.absolutePath
    }
}