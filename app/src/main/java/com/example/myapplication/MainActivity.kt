package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.ImageEntity

class MainActivity : AppCompatActivity() {

    var rcView: RecyclerView? = null
    var adapter: Adapter? = null
    var tvNumber: TextView? = null
    var count: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initListener()
        openGallery()
    }

    private fun initListener() {
        adapter?.onItemClickListener = { item, position ->
            if (item.isCheck == true) {
                item.isCheck = false
                count--;
            } else {
                item.isCheck = true
                count++
            }
            if (count != 0) {
                item.count = count
                tvNumber?.text = count.toString()
            }else
                tvNumber?.text = ""
            adapter?.notifyItemChanged(position)
        }
    }

    private fun initView() {
        rcView = findViewById(R.id.rcView)
        tvNumber = findViewById(R.id.tvNumber)
        val gridLayoutManager = GridLayoutManager(this, 3)
        rcView?.layoutManager = gridLayoutManager
        adapter = Adapter()
        rcView?.adapter = adapter
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if ((ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED)
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), 1
                )
            } else {
                var imageEntity = loadImagesfromSDCard()
                var listImage = ArrayList<ImageEntity>()
                for (image in imageEntity) {
                    val item = ImageEntity()
                    item.uri = image
                    listImage.add(item)
                }
                adapter?.setListImage1(
                    listImage
                )
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    1
                )
            } else {
                var imageEntity = loadImagesfromSDCard()
                var listImage = ArrayList<ImageEntity>()
                for (image in imageEntity) {
                    val item = ImageEntity()
                    item.uri = image
                    listImage.add(item)
                }
                adapter?.setListImage1(
                    listImage
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun loadImagesfromSDCard(): ArrayList<String> {
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor?
        val column_index_folder_name: Int
        val listOfAllImages = ArrayList<String>()
        var absolutePathOfImage: String? = null

        val projection =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        cursor = this.contentResolver.query(uri, projection, null, null, null)

        val column_index_data: Int? = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        while (cursor?.moveToNext() == true && listOfAllImages.size < 10) {
            absolutePathOfImage = cursor.getString(column_index_data ?: 0)
            listOfAllImages.add(absolutePathOfImage)
        }
        Log.e("TAG", "loadImagesfromSDCard: " + listOfAllImages)
        return listOfAllImages
    }
}