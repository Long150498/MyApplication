package com.example.myapplication

import android.content.ContentResolver
import android.content.Intent
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
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.myapplication.data.ImageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File


class Adapter : RecyclerView.Adapter<Adapter.VH>() {
    var listImage: ArrayList<ImageEntity>? = null
    var onItemClickListener: ((item: ImageEntity, Int) -> Unit)? = null
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

        fun onBind(context: View, itemUri: Uri?) {
            val f = File(itemUri?.path)
            val bitmap = decodeSampledBitmapFromFile(itemUri.toString(),500,500)
//            val bitmap = BitmapFactory.decodeFile(f.path)

            image.setImageBitmap(bitmap)
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
            holder.itemView.context.startActivity(Intent(holder.itemView.context, DetailImageActivity::class.java).putExtra("URI", item?.uri))
            return@setOnLongClickListener true
        }
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

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        holder.image.setImageBitmap(null)
    }
}