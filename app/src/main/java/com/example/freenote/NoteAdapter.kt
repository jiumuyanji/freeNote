package com.example.freenote

import android.content.Context
import android.content.Intent
import android.graphics.Color
import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter(val context:Context,val noteList:List<Note2>,val owner:String):RecyclerView.Adapter<NoteAdapter.ViewHolder>(){
    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val noteImage:ImageView=view.findViewById(R.id.noteImage)
        val noteTitle:TextView=view.findViewById(R.id.noteTitle)
        val noteTime:TextView=view.findViewById(R.id.noteTime)
        val userName:TextView=view.findViewById(R.id.userName4)
        val leftTime:TextView=view.findViewById(R.id.leftTime)
        val clockImage:ImageView=view.findViewById(R.id.clockImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(context).inflate(R.layout.note_item,parent,false)
        val holder=ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position=holder.adapterPosition
            val note=noteList[position]
            val intent=Intent(context,readNote::class.java)
            intent.putExtra("owner",owner)
            intent.putExtra("noteId",note.noteId)
            intent.putExtra("noteTitle",note.title)
            intent.putExtra("noteTime",note.noteTime)
            intent.putExtra("noteArea",note.noteArea)
            intent.putExtra("noteDetail",note.detail)
            intent.putExtra("userName",note.userName)
            intent.putExtra("friendList",note.friendList)
            intent.putExtra("noteType",note.type)
            intent.putExtra("cancel",note.cancel)
            context.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note=noteList[position]
        holder.noteTitle.text=note.title
        holder.noteTime.text=note.noteTime
        holder.userName.text="发布人： "+note.userName
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val d1= df.parse(note.noteTime)
        val d2= Date(System.currentTimeMillis())
        val diff = d1.time-d2.time
        val days = diff/(1000*60*60*24)
        val hours = (diff-days*(1000*60*60*24))/(1000*60*60)
        val minutes =( (diff-days*(1000*60*60*24))-hours*(1000*60*60))/(1000*60)
        if(diff<=0){
            holder.leftTime.text="已过期"
            holder.leftTime.setTextColor(Color.parseColor("#000000"))
        }else if(days!=0.toLong()){
            holder.leftTime.text="还剩："+days.toString()+"天"+hours.toString()+"小时"+minutes.toString()+"分钟"
            holder.leftTime.setTextColor(Color.parseColor("#388E3C"))
        }else if(hours!=0.toLong()){
            holder.leftTime.text="还剩："+hours.toString()+"小时"+minutes.toString()+"分钟"
            holder.leftTime.setTextColor(Color.parseColor("#FBC02D"))
        }else {
            holder.leftTime.text="还剩："+minutes.toString()+"分钟"
            holder.leftTime.setTextColor(Color.parseColor("#A12703"))
        }
        if(note.type=="会议"){
            Glide.with(context).load(R.drawable.meeting).into(holder.noteImage)
        }
        else if(note.type=="出差"){
            Glide.with(context).load(R.drawable.business).into(holder.noteImage)
        }
        else if(note.type=="旅游"){
            Glide.with(context).load(R.drawable.trip).into(holder.noteImage)
        }
        else if(note.type=="聚会"){
            Glide.with(context).load(R.drawable.meeting2).into(holder.noteImage)
        }
        else if(note.type=="购物"){
            Glide.with(context).load(R.drawable.shopping).into(holder.noteImage)
        }
        else if(note.type=="接送"){
            Glide.with(context).load(R.drawable.huoche).into(holder.noteImage)
        }
        else if(note.type=="见面"){
            Glide.with(context).load(R.drawable.meet).into(holder.noteImage)
        }
        else {
            Glide.with(context).load(R.drawable.papers).into(holder.noteImage)
        }


        if(note.cancel=="1"){
            Glide.with(context).load(R.drawable.cancel).into(holder.clockImage)
            holder.leftTime.text="已取消！"
            holder.leftTime.setTextColor(Color.parseColor("#A12703"))
        }
        else if(note.clock=="1"&&diff>=0){
            Glide.with(context).load(R.drawable.oclock).into(holder.clockImage)
        }
        else{
            Glide.with(context).load(R.drawable.cclock).into(holder.clockImage)
        }
    }

    override fun getItemCount()=noteList.size
}