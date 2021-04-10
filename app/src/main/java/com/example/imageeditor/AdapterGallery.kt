package com.example.imageeditor

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import com.pixxo.photoeditor.ImageModel


class AdapterGallery(var list: MutableList<ImageModel?>) : RecyclerView.Adapter<AdapterGallery.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_gallery, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.apply {
            val imgFile = File(list.get(position)!!.path)
            Log.e("vvvvvv",imgFile.absolutePath)
            val constraintData :ConstraintLayout = findViewById(R.id.const_item)
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                val myImage: ImageView = findViewById<ImageView>(R.id.imgGallery)
                myImage.setImageBitmap(myBitmap)
            }
            if (list.get(position)!!.isSelected)
            {
                constraintData.setBackgroundColor(resources.getColor(R.color.red))
            }
            else
            {
                constraintData.setBackgroundColor(resources.getColor(android.R.color.transparent))
            }
        }
    }

    override fun getItemCount(): Int = list.size
}