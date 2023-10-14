package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.scale
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.ImageEntity
import java.io.ByteArrayOutputStream
import java.io.File


class Adapter : RecyclerView.Adapter<Adapter.VH>() {
    var listImage: ArrayList<ImageEntity>? = null
    var onItemClickListener: ((item: ImageEntity, Int) -> Unit)? = null
    val stream = ByteArrayOutputStream()

    fun setListImage1(listInput: ArrayList<ImageEntity>?) {
        listImage = ArrayList()
        if (listInput != null) {
            listImage?.addAll(listInput)
        }
        notifyDataSetChanged()
    }

    class VH(val view: View) : RecyclerView.ViewHolder(view) {

        val image: ImageView by lazy {
            view.findViewById(R.id.image)
        }
        val tvNum: TextView by lazy {
            view.findViewById(R.id.tvNum)
        }

        fun onBind(stream: ByteArrayOutputStream, itemUri: Uri?) {
            val f = File(itemUri?.path)
            val bitmap = BitmapFactory.decodeFile(f.path).scale(50,50,true)
            bitmap.compress(Bitmap.CompressFormat.JPEG,1,stream)
            image.setImageBitmap(bitmap)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.adapter, parent, false))
    }

    override fun getItemCount(): Int {
        return listImage?.size ?: 0
    }

    fun addData(mList: ArrayList<String>) {
        val dataFinish = ArrayList<ImageEntity>()
        if (mList != null) {
            for (item in mList) {
                var data = ImageEntity()
                data.uri = item
                dataFinish.add(data)
            }
            this.listImage = dataFinish
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = listImage?.get(position)
        val itemUri = Uri.parse(item?.uri)
        holder.itemView.setOnLongClickListener {
            holder.itemView.context.startActivity(
                Intent(
                    holder.itemView.context,
                    DetailImageActivity::class.java
                ).putExtra("URI", item?.uri)
            )
            return@setOnLongClickListener true
        }
        holder.onBind(stream, itemUri)
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
}