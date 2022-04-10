package com.example.freenote

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class clockAdapter(val context: Context, val clockList:List<inputClockOfShow>, val type:String,var alarmManager: AlarmManager): RecyclerView.Adapter<clockAdapter.ViewHolder>(){
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val clockTime: TextView =view.findViewById(R.id.clockTime)
        val clockTitle: TextView =view.findViewById(R.id.clockTitle)
        val name:TextView = view.findViewById(R.id.name)
        val set:TextView = view.findViewById(R.id.set)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.clock_item,parent,false)
//        val holder=ViewHolder(view)
//        val position=holder.adapterPosition
//        val clock=clockList[position]
//        if(type=="people"){
//            holder.itemView.setOnClickListener {
//                val intent= Intent(context,AlarmReceiver::class.java)
//                //下面的msg内容要改成记录的内容
//                intent.putExtra("title",clock.title)
//                intent.putExtra("userName",clock.userName)
//                intent.putExtra("owner",clock.userName)
//                //requestCode要改成该记录的id，不然闹钟取消不掉
//                val pendingIntent= PendingIntent.getBroadcast(context,clock.id,intent,0)
//                val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
//                val calendar = Calendar.getInstance()
//                calendar.time=df.parse(clock.time)
//                AlertDialog.Builder(context).apply {
//                    setTitle("是否打开提醒")
//                    setCancelable(true)
//                    setPositiveButton("打开提醒"){ _, _ ->
//                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent)
//                    }
//                    setNegativeButton("关闭提醒"){_, _->
//                        alarmManager.cancel(pendingIntent)
//                    }
//                    show()
//                }
//            }
//            return holder
//        }else{
//            return ViewHolder(view)
//        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clock=clockList[position]
        holder.clockTime.text=clock.time
        holder.clockTitle.text=clock.title
        holder.name.text="所有者："+clock.userName
        if(clock.set=="0"){
            holder.set.text = "未收到共享提醒邀请"
            holder.set.setTextColor(Color.parseColor("#A12703"))
        }else{
            holder.set.text = "已收到共享提醒邀请"
            holder.set.setTextColor(Color.parseColor("#388E3C"))
        }
        if(type=="people"){
            holder.itemView.setOnClickListener {
                val intent= Intent(context,AlarmReceiver::class.java)
                //下面的msg内容要改成记录的内容
                intent.putExtra("title",clock.title)
                intent.putExtra("userName",clock.userName)
                intent.putExtra("owner",clock.userName)
                //requestCode要改成该记录的id，不然闹钟取消不掉
                val pendingIntent= PendingIntent.getBroadcast(context,clock.id,intent,0)
                val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val calendar = Calendar.getInstance()
                calendar.time=df.parse(clock.time)
                AlertDialog.Builder(context).apply {
                    setTitle("是否打开提醒")
                    setCancelable(true)
                    setPositiveButton("打开提醒"){ _, _ ->
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent)
                    }
                    setNegativeButton("关闭提醒"){_, _->
                        alarmManager.cancel(pendingIntent)
                    }
                    show()
                }
            }
        }
    }

    override fun getItemCount()=clockList.size
}