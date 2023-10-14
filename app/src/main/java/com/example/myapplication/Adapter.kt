package com.example.myapplication

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.ImageEntity
import java.io.File


class Adapter : RecyclerView.Adapter<Adapter.VH>() {
    var listImage: ArrayList<ImageEntity>? = null
    var onItemClickListener: ((item: ImageEntity, Int) -> Unit)? = null
    val listSelected = mutableListOf<ImageEntity>()
    fun setListImage1(listInput: ArrayList<ImageEntity>?) {
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
        val tvNum: TextView by lazy {
            view.findViewById<TextView>(R.id.tvNum)
        }
        private val layoutRoot: FrameLayout by lazy {
            view.findViewById<FrameLayout>(R.id.llRoot)
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
        val itemUri = Uri.parse(item?.uri)
        holder.onBind(holder.itemView, itemUri)
        if (item?.isCheck == true) {
            holder.tvNum.visibility = View.VISIBLE
        } else {
            holder.tvNum.visibility = View.GONE
        }
        holder.tvNum.text = item?.count.toString()
        holder.itemView.setOnClickListener {
            if (item != null) {
                onItemClickListener?.invoke(item, position)
            }
        }
    }

    public fun getItemSelected(): Int {
        return listSelected.size + 1
    }
}