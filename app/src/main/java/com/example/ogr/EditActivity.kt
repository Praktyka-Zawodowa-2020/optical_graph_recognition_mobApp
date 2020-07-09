package com.example.ogr

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class EditActivity : AppCompatActivity() {
    val EXTRA_BITMAP = "com.example.ogr.BITMAP"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val imageBitmap: Bitmap? = intent.getParcelableExtra(EXTRA_BITMAP) as Bitmap?
        if (imageBitmap != null)
            imageView.setImageBitmap(imageBitmap)
    }

}