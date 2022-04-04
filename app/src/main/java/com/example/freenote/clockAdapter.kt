package com.example.freenote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class clockAdapter(val context: Context, val clockList:List<outputClock>): RecyclerView.Adapter<clockAdapter.ViewHolder>(){
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val clockTime: TextView =view.findViewById(R.id.clockTime)
        val clockTitle: TextView =view.findViewById(R.id.clockTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.clock_item,parent,false)
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
        val clock=clockList[position]
        holder.clockTime.text=clock.time
        holder.clockTitle.text=clock.title
    }

    override fun getItemCount()=clockList.size
}