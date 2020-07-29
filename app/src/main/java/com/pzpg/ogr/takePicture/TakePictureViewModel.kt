package com.pzpg.ogr.takePicture

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel

class TakePictureViewModel: ViewModel() {

    var image: Bitmap? = null
    var uri: Uri? = null
    var path: String? = null

}