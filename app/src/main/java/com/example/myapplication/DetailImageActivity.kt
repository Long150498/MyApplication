package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView

class DetailImageActivity : AppCompatActivity() {
    var image: TouchImageView? = null
    var back: AppCompatImageView? = null
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_image)
        initViews()
        initAction()
    }

    private fun initViews() {
        if (intent.hasExtra("URI")) {
            uri = Uri.parse(intent.getStringExtra("URI"))
        }
        image = findViewById<TouchImageView>(R.id.ivDetail)
        back = findViewById<TouchImageView>(R.id.ivBack)
        val bitmap = decodeSampledBitmapFromFile(uri.toString(), 500, 500)
        image?.setImageBitmap(bitmap)
    }

    private fun initAction() {
        back?.setOnClickListener {
            finish()
        }
    }

    private fun decodeSampledBitmapFromFile(
        filePath: String,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}