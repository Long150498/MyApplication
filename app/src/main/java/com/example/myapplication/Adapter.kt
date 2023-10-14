package com.example.myapplication

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class Adapter : RecyclerView.Adapter<Adapter.VH>() {
    var listImage: ArrayList<String>? = null
    fun setListImage1(listInput: ArrayList<String>?) {
        listImage = ArrayList()
        if (listInput != null) {
            listImage?.addAll(listInput)
        }
        notifyDataSetChanged()
    }

    class VH(val view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView by lazy {
            view.findViewById<ImageView>(R.id.image)
        }

        fun uriToBitmap(uri: Uri, contentResolver: ContentResolver): Bitmap? {
            return try {
                val inputStream = contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        fun onBind(context: View, itemUri: Uri?) {
            val f = File(itemUri?.path)
//            val bitmap = BitmapFactory.decodeFile(f.path)
            val bmOptions = BitmapFactory.Options()
            var bitmap = BitmapFactory.decodeFile(f.path, bmOptions)
            bitmap = Bitmap.createScaledBitmap(bitmap!!, 100, 100, true)
            image.setImageBitmap(bitmap)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.adapter, parent, false))
    }

    override fun getItemCount(): Int {
        return listImage?.size ?: 0
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = listImage?.get(position)
        val itemUri = Uri.parse(item)
        holder.onBind(holder.itemView, itemUri)
    }
}