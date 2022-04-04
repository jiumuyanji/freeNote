package com.example.freenote

import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(val context:Context,val noteList:List<Note2>):RecyclerView.Adapter<NoteAdapter.ViewHolder>(){
    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val noteImage:ImageView=view.findViewById(R.id.noteImage)
        val noteTitle:TextView=view.findViewById(R.id.noteTitle)
        val noteTime:TextView=view.findViewById(R.id.noteTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(context).inflate(R.layout.note_item,parent,false)
        val holder=ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position=holder.adapterPosition
            val note=noteList[position]
            val intent=Intent(context,readNote::class.java)
            intent.putExtra("noteTitle",note.title)
            intent.putExtra("noteTime",note.noteTime)
            intent.putExtra("noteArea",note.noteArea)
            intent.putExtra("noteDetail",note.detail)
            intent.putExtra("userName",note.userName)
            intent.putExtra("friendList",note.friendList)
            context.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note=noteList[position]
        holder.noteTitle.text=note.title
        holder.noteTime.text=note.noteTime
        Glide.with(context).load(R.drawable.huoche).into(holder.noteImage)
    }

    override fun getItemCount()=noteList.size
}