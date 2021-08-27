package com.example.freenote

import android.content.Context
import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(val context:Context,val noteList:List<Note>):RecyclerView.Adapter<NoteAdapter.ViewHolder>(){
    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val noteImage:ImageView=view.findViewById(R.id.noteImage)
        val noteTitle:TextView=view.findViewById(R.id.noteTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(context).inflate(R.layout.note_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note=noteList[position]
        holder.noteTitle.text=note.title
        Glide.with(context).load(note.imageId).into(holder.noteImage)
    }

    override fun getItemCount()=noteList.size
}