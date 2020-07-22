package com.pzpg.ogr.ui.takePicture

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.pzpg.ogr.*
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
    private fun createImageFile(prefix: String, extension: String): File {
        // Create an image file name
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            prefix, /* prefix */
            extension, /* suffix */
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
                    createImageFile("photo_", ".jpg")
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

        // Code taken from answer on http://stackoverflow.com/questions/15699299/android-edit-image-intent
        val flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION

        val editIntent = Intent(Intent.ACTION_EDIT)
        editIntent.setDataAndType(photoUri, "image/*")
        editIntent.addFlags(flags)
        editIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

        // This work-around allows the intent to access our private FileProvider storage.
        // Code taken from http://stackoverflow.com/questions/24835364/android-open-private-file-with-third-party-app

        val resInfoList: List<ResolveInfo> = requireActivity().packageManager
            .queryIntentActivities(editIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName: String = resolveInfo.activityInfo.packageName
            requireActivity().grantUriPermission(packageName, photoUri, flags)
        }

        startActivityForResult(Intent.createChooser(editIntent, null), EDIT_INTENT)

        Toast.makeText(requireContext(), "Click on image", Toast.LENGTH_SHORT).show()
    }

    private fun openGallery(view: View){
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickPictureGallery->
            pickPictureGallery.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(pickPictureGallery, REQUEST_GALLERY_PHOTO);
        }
    }

    fun getRealPathFromURI(contentURI: Uri?): String? {
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = requireActivity().managedQuery(
            contentURI, projection, null,
            null, null
        )
            ?: return null
        val column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        return if (cursor.moveToFirst()) {
            // cursor.close();
            cursor.getString(column_index)
        } else null
        // cursor.close();
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

            if (imageURI != null) {
                val realPath = getRealPathFromURI(imageURI)
                Log.i("REQUEST_GALLERY_PHOTO",realPath.toString())
                val pickPhoto = File(realPath)

                //val pickPhoto = File(imageURI.toString())
                val newFile = createImageFile("gallery_", ".jpg")

                pickPhoto.copyTo(newFile, overwrite = true)
                photoUri = newFile.toUri()

            }
        }else if(requestCode == EDIT_INTENT){

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

}