package com.example.freenote

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PictureAdapter (val context: Context, val picturesList:List<Bitmap>): RecyclerView.Adapter<PictureAdapter.ViewHolder>(){
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val image:ImageView = view.findViewById(R.id.picture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.picture_item,parent,false)
       return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var picture = picturesList[position]
        holder.image.setImageBitmap(picture)
        holder.image.setOnClickListener {
            AlertDialog.Builder(context).apply {
                val view2 =LayoutInflater.from(context).inflate(R.layout.dialog_to_show_picture,null)
                val imageOfShow = view2.findViewById<ImageView>(R.id.imageOfShow)
                imageOfShow.setImageBitmap(picturesList[position])
                setCancelable(true)
                setView(view2)
                imageOfShow.setOnClickListener {
                }
                show()
            }
        }
//        holder.itemView.setOnClickListener {
//            if(picture==null){
//                Log.d("23123131","11")
//            }else{
//                Log.d("23123131","22")
//            }

//        }
    }

    override fun getItemCount()=picturesList.size
}