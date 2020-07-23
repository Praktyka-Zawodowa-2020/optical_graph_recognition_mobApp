package com.pzpg.ogr.ui.takePicture

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.pzpg.ogr.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class TakePictureFragment : Fragment() {

    private val TAG = "TakePictureFragment"
    private lateinit var viewModel: TakePictureViewModel

    private lateinit var imageView: ImageView
    private var currentPhotoPath: String? = null
    private var photoUri: Uri? = null

    private lateinit var inputPFD: ParcelFileDescriptor

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


        imageView.setOnClickListener {
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

    private fun setImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.NONE )
            .skipMemoryCache(true)
            .into(imageView)
    }


    private fun goProcess() {
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if (account != null) {
            if (photoUri != null) {
                Intent(requireContext(), ProcessActivity::class.java).also {
                    it.putExtra("EXTRA_PHOTO_PATH", currentPhotoPath)
                    startActivity(it)
                }
            } else {
                Toast.makeText(requireContext(), "Need a photo", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(), "Need a google account", Toast.LENGTH_LONG).show()
        }
    }


    private fun setupPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
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
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(prefix,extension,storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun takePhoto(view: View) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile("photo_", ".jpg")
                } catch (ex: IOException) {
                    Log.i("EXCEPTION", ex.toString())
                    null
                }

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

    private fun editPhoto(view: View) {
        if(photoUri != null){
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
        }else{
            Toast.makeText(requireContext(), "Need a photo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery(view: View) {
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).also { pickPictureGallery ->
            pickPictureGallery.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(
                Intent.createChooser(pickPictureGallery, null),
                REQUEST_GALLERY_PHOTO
            );
        }
    }

    private fun getRealPathFromURI(contentURI: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = requireActivity().managedQuery(
            contentURI, projection, null,
            null, null
        )
            ?: return null
        val column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        return if (cursor.moveToFirst()) {
            cursor.getString(column_index)
        } else null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG, "onActivityResult")

        when (requestCode) {
            REQUEST_CAMERA_PHOTO -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    if (currentPhotoPath != null) {
                        val file = File(currentPhotoPath!!)
                        val imageBitmap = MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver,
                            Uri.fromFile(file)
                        )
                        imageView.setImageBitmap(imageBitmap)
                        viewModel.image = imageBitmap
                    }
                }
            }
            REQUEST_GALLERY_PHOTO -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val imageURI: Uri? = data?.data as Uri
                    if (imageURI != null) {

                        val destinationFile: File? = try {
                            createImageFile("gallery_", ".jpg")
                        }catch (e: IOException){
                            Log.e("Creating file", e.toString())
                            null
                        }

                        destinationFile?.also {
                            photoUri = FileProvider.getUriForFile(
                                requireActivity(),
                                "com.pzpg.org.fileprovider",
                                it
                            )



                            val source = requireActivity().contentResolver.openInputStream(imageURI)
                            val destination: OutputStream = FileOutputStream(destinationFile)

                            val buffer = ByteArray(1024)
                            var length: Int

                            while (source!!.read(buffer).also { length = it } > 0) {
                                destination.write(buffer, 0, length)
                            }

                            source.close()
                            destination.close()


                            setImage(photoUri!!)
                        }
                    }
                }
            }
            EDIT_INTENT -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    if(photoUri != null)
                        setImage(photoUri!!)
                }
            }
        }
    }
}
