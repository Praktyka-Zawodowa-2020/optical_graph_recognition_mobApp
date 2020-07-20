package com.pzpg.ogr.ui.takePicture

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel

class TakePictureViewModel: ViewModel() {


    var image: Bitmap? = null

    override fun onCleared() {
        super.onCleared()
        Log.i("TakePictureViewModel", "is destroyed")
    }
}