package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView


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
        fun onBind(context: Context, itemUri: Uri?) {

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
        holder.onBind(holder.itemView.context, itemUri)
    }

}