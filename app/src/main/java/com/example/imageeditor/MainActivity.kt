package com.example.imageeditor

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.pixxo.photoeditor.EditImageActivity
import com.pixxo.photoeditor.ImageModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    var gallery: AdapterGallery? = null
    var isMultiSelect: Boolean = false;
    var list:MutableList<ImageModel?> = arrayListOf()
    var imagelist:MutableList<ImageModel?> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val staggeredGridLayoutManager = GridLayoutManager(this, 2)
        imagelist = getImagesPath(this)
        Log.e("images", imagelist.toString())
        Log.e("imagesnumber", imagelist!!.size.toString())
        gallery = AdapterGallery(imagelist!!)
        rec_gallery.apply {
            adapter = gallery
            layoutManager = staggeredGridLayoutManager
        }

        rec_gallery.addOnItemTouchListener(
            RecyclerTouchListener(
                this,
                rec_gallery,
                object : RecyclerTouchListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        if (isMultiSelect)
                            multi_select(position)
                        updateUI()

                    }

                    override fun onLongItemClick(view: View?, position: Int) {
                        if (!isMultiSelect) {
                            list.clear()
                            isMultiSelect = true
                        }

                        multi_select(position)
                        updateUI()
                    }
                })
        )

        iv_done.setOnClickListener {
            val editImageIntent = Intent(applicationContext, EditImageActivity::class.java)
            editImageIntent.putExtra("LIST", list as Serializable)
            startActivity(editImageIntent)
        }

    }

    private fun updateUI() {
        if (list.size<1)
        {
            iv_done.visibility=View.GONE
        }
        else
        {
            iv_done.visibility=View.VISIBLE
        }
    }

    fun getImagesPath(activity: Activity): MutableList<ImageModel?> {
        val uri: Uri
        val listOfAllImages = ArrayList<ImageModel?>()
        val cursor: Cursor?
        val column_index_data: Int
        val column_index_folder_name: Int
        var PathOfImage: String? = null
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        cursor = activity.contentResolver.query(
            uri, projection, null,
            null, null
        )
        column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor!!.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data)
            listOfAllImages.add(ImageModel(PathOfImage))
        }
        return listOfAllImages.asReversed()
    }

    fun multi_select(position: Int) {
        var positionInLst : Int? = null
        var isFound : Boolean = false
        if (list.size>0)
        {
            for (i in 0..list.size-1)
            {
                if (list[i]!!.path.equals(imagelist[position]!!.path))
                {
                    positionInLst = i!!
                    isFound =true
                    break
                }
            }
        }
        if (isFound)
        {
            list.removeAt(positionInLst!!)
            imagelist.get(position)!!.isSelected=false
        }
        else
        {
            list.add(imagelist[position]!!)
            imagelist.get(position)!!.isSelected=true
        }

        gallery!!.notifyDataSetChanged()
    }

}