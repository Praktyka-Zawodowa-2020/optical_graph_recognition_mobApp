package com.pzpg.ogr.ui.takePicture

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.pzpg.ogr.ProcessActivity
import com.pzpg.ogr.R
import com.pzpg.ogr.REQUEST_CAMERA_PHOTO
import com.pzpg.ogr.REQUEST_GALLERY_PHOTO
import java.io.File
import java.io.IOException


class TakePictureFragment : Fragment() {

    private val TAG = "TakePictureFragment"
    private lateinit var viewModel: TakePictureViewModel

    private lateinit var  imageView: ImageView
    private var currentPhotoPath: String? = null
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_take_picture, container, false)
        imageView = view.findViewById(R.id.imageView)

        viewModel = ViewModelProviders.of(this).get(TakePictureViewModel::class.java)
        imageView.setImageBitmap(viewModel.image)


        imageView.setOnClickListener{
            editPhoto(it)
        }
        view.findViewById<Button>(R.id.button_camera).setOnClickListener {
            takePhoto(it)
        }
        view.findViewById<Button>(R.id.button_gallery).setOnClickListener {
            openGallery(it)
        }
        view.findViewById<Button>(R.id.button_process).setOnClickListener {
            goProcess()
        }

        return view
    }


    private fun goProcess(){
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if(account != null){
            if (currentPhotoPath != null){
                Intent(requireContext(), ProcessActivity::class.java).also {
                    it.putExtra("EXTRA_PHOTO_PATH", currentPhotoPath)
                    startActivity(it)
                }
            }else{
                Toast.makeText(requireContext(), "Need a photo", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(requireContext(), "Need a google account", Toast.LENGTH_LONG).show()
        }

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
            Log.d("setupPermissions", "PERMISSION GRANTED")
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
                    photoUri = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.pzpg.org.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA_PHOTO)
                }
            }
        }
    }

    private fun editPhoto(view: View){
        /*if(photoUri != null) {
            Intent(Intent.ACTION_EDIT).also { editPhoto ->
                editPhoto.setDataAndType(photoUri, "image/*");
                editPhoto.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                editPhoto.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivity(Intent.createChooser(editPhoto, null));
            }
        }*/

         */

        Toast.makeText(requireContext(), "Click on image", Toast.LENGTH_SHORT).show()
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
            if (currentPhotoPath != null) {
                val file = File(currentPhotoPath!!)
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver,
                    Uri.fromFile(file)
                )
                imageView.setImageBitmap(imageBitmap)
                viewModel.image = imageBitmap
            }
        }else if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == AppCompatActivity.RESULT_OK){
            val imageURI: Uri? = data?.data as Uri
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            if (imageURI != null) {

                val cursor: Cursor? = requireActivity().contentResolver.query(
                    imageURI,
                    filePathColumn, null, null, null
                )

                if (cursor != null) {
                    cursor.moveToFirst()
                    val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                    val picturePath: String = cursor.getString(columnIndex)
                    imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))
                    cursor.close()
                    currentPhotoPath = picturePath
                    Log.i("REQUEST_GALLERY_PHOTO", picturePath)
                }



            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

}