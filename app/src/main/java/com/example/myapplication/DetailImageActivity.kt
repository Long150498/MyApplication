package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class DetailImageActivity : AppCompatActivity() {
    var image: TouchImageView? = null
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_image)
        initViews()
    }

    private fun initViews() {
        if (intent.hasExtra("URI")) {
            uri = Uri.parse(intent.getStringExtra("URI"))
        }
        image = findViewById<TouchImageView>(R.id.ivDetail)
        val f = File(uri?.path)
        val bmOptions = BitmapFactory.Options()
        var bitmap = BitmapFactory.decodeFile(f.path, bmOptions)
        bitmap = Bitmap.createScaledBitmap(bitmap!!, 100, 100, true)
        image?.setImageBitmap(bitmap)
    }
}