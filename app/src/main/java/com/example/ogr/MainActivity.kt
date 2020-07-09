package com.example.ogr

import android.R.attr
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.net.URI


class MainActivity : AppCompatActivity() {

    val REQUEST_GALLERY_PHOTO = 1
    val REQUEST_IMAGE_CAPTURE = 2
    val REQUEST_TAKE_PHOTO = 3
    val EXTRA_BITMAP = "com.example.ogr.BITMAP"
    private var iamgeBitmap: Bitmap? = null
    lateinit var currentPhotoPath: String

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

    fun openGallery(view: View){
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickPictureGallery->
            pickPictureGallery.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(pickPictureGallery, REQUEST_GALLERY_PHOTO);
        }
    }

    fun takePhoto(view: View){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.org.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

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
            val file = File(currentPhotoPath)
            val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
            imageView.setImageBitmap(imageBitmap)
        }else if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK){
            val imageURI: Uri? = data?.data as Uri
            if (imageURI != null) {
                val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageURI)
                imageView.setImageBitmap(imageBitmap)
            }
        }
    }
}