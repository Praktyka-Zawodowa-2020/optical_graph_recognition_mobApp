package com.pzpg.ogr.ui.takePicture

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.pzpg.ogr.R
import com.pzpg.ogr.REQUEST_CAMERA_PHOTO
import com.pzpg.ogr.REQUEST_GALLERY_PHOTO
import java.io.File
import java.io.IOException


class TakePictureFragment : Fragment() {

    private val TAG = "TakePictureFragment"
    private lateinit var viewModel: TakePictureViewModel

    private lateinit var  imageView: ImageView
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_take_picture, container, false)
        imageView = view.findViewById<ImageView>(R.id.imageView)

        viewModel = ViewModelProviders.of(this).get(TakePictureViewModel::class.java)
        imageView.setImageBitmap(viewModel.image)



        view.findViewById<Button>(R.id.button_camera).setOnClickListener {
            takePhoto(it)
        }
        view.findViewById<Button>(R.id.button_gallery).setOnClickListener {
            openGallery(it)
        }
        view.findViewById<Button>(R.id.button_process).setOnClickListener {
            TODO("NOT IMPLEMENT")
        }

        return view
    }


    private fun setupPermissions(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                1000
            )
        } else {
            Log.d("DB", "PERMISSION GRANTED")
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_test_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun takePhoto(view: View){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
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
                        requireActivity(),
                        "com.pzpg.org.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA_PHOTO)
                }
            }
        }
    }

    private fun openGallery(view: View){
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickPictureGallery->
            pickPictureGallery.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(pickPictureGallery, REQUEST_GALLERY_PHOTO);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG, "onActivityResult")

        if (requestCode == REQUEST_CAMERA_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {

            val file = File(currentPhotoPath)
            val imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, Uri.fromFile(file))
            imageView.setImageBitmap(imageBitmap)
            viewModel.image = imageBitmap
        }else if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == AppCompatActivity.RESULT_OK){
            val imageURI: Uri? = data?.data as Uri
            if (imageURI != null) {
                val imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageURI)
                imageView.setImageBitmap(imageBitmap)
                viewModel.image = imageBitmap
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

}