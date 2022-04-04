package com.example.freenote

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FriendAdapter (val context: Context, val friendList:List<Friend>): RecyclerView.Adapter<FriendAdapter.ViewHolder>(){
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val friendImage: ImageView =view.findViewById(R.id.friendImage)
        val friendName: TextView =view.findViewById(R.id.friendName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.friend_item,parent,false)
//        val holder=ViewHolder(view)
//        holder.itemView.setOnClickListener {
//            val position=holder.adapterPosition
//            val note=noteList[position]
//            val intent= Intent(context,readNote::class.java)
//            intent.putExtra("noteTitle",note.title)
//            intent.putExtra("noteDetail",note.detail)
//            intent.putExtra("userName",note.userName)
//            context.startActivity(intent)
//        }
//        return holder
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend=friendList[position]
        holder.friendName.text=friend.username
        Glide.with(context).load(R.drawable.nav_account).into(holder.friendImage)
    }

    override fun getItemCount()=friendList.size
}