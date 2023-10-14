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
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.ImageEntity

class MainActivity : AppCompatActivity() {

    var rcView: RecyclerView? = null
    var adapter: Adapter? = null
    var tvNumber: TextView? = null
    var scrollView : NestedScrollView?=null
    var index: Int = 0
    var count: Int = 0
    val listOfAllImages = ArrayList<String>()
    val arrayListMore = ArrayList<String>()
    private var loadMore = false
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
            } else
                tvNumber?.text = ""
            adapter?.notifyItemChanged(position)
        }
    }

    private fun initView() {
        rcView = findViewById(R.id.rcView)
        tvNumber = findViewById(R.id.tvNumber)
        scrollView = findViewById(R.id.scrollView)
        val gridLayoutManager = GridLayoutManager(this, 3)
        rcView?.layoutManager = gridLayoutManager
        adapter = Adapter()
        rcView?.adapter = adapter
        scrollView?.setOnScrollChangeListener(object : View.OnScrollChangeListener {
            override fun onScrollChange(p0: View?, p1: Int, p2: Int, p3: Int, p4: Int) {
                if (scrollView!!.getChildAt(0).bottom <= scrollView!!.height + scrollView!!.scrollY) {
                    //scroll view is at bottom
                    loadMore = true
                    index += 1
                    getList(index)
                } else {
                    //scroll view is not at bottom
                }
            }
        })
    }

    private fun  chopped(
        list: ArrayList<String>,
        lengthInPage: Int
    ): ArrayList<ArrayList<String>>? {
        val parts = ArrayList<ArrayList<String>>()
        val N = list.size
        var i = 0
        while (i < N) {
            parts.add(
                ArrayList(
                    list.subList(i, Math.min(N, i + lengthInPage))
                )
            )
            i += lengthInPage
        }
        return parts
    }

    private fun getList(length: Int) {
        var list: ArrayList<ArrayList<String>> =
            ArrayList<ArrayList<String>>()
        if (loadMore) {
            loadMore = false
            list = chopped(listOfAllImages, 10)!!
            if (list != null && list.size > 0) {
                if (length < list.size) {
                    arrayListMore.addAll(list[length])
                    adapter?.addData(arrayListMore)
                }
            }
        }
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
                loadMore = true;
                loadImagesfromSDCard()
                getList(index)
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
//                var imageEntity = loadImagesfromSDCard()
//                var listImage = ArrayList<ImageEntity>()
//                for (image in imageEntity) {
//                    val item = ImageEntity()
//                    item.uri = image
//                    listImage.add(item)
//                }
//                adapter?.setListImage1(
//                    listImage
//                )
                loadMore = true;
                loadImagesfromSDCard()
                getList(index)
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
        var absolutePathOfImage: String? = null

        val projection =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        cursor = this.contentResolver.query(uri, projection, null, null, null)

        val column_index_data: Int? = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
//        column_index_folder_name = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor?.moveToNext() == true) {
            absolutePathOfImage = cursor.getString(column_index_data ?: 0)
            listOfAllImages.add(absolutePathOfImage)
        }
        Log.e("TAG", "loadImagesfromSDCard: " + listOfAllImages)
        return listOfAllImages
    }
}