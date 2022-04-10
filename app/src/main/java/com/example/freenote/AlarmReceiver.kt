package com.example.freenote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class AlarmReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent){
//        提示闹钟触发的toast
//        Toast.makeText(context , "闹钟", Toast.LENGTH_LONG).show()
        //data是用来作为闹钟触发时信息提醒的信息
        val data=intent.getStringExtra("title")
        val userName = intent.getStringExtra("userName")
        val owner = intent.getStringExtra("owner")
        val intent1=Intent(context,clock::class.java)
        //不加这个flag没法在无activity的情况下打开activity
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent1.putExtra("title",data)
        intent1.putExtra("userName",userName)
        intent1.putExtra("owner",owner)
        context.startActivity(intent1)

//        val mediaPlayer  = MediaPlayer.create(context,R.raw.tishi)
//        mediaPlayer.start()
//        val alertDialog=AlertDialog.Builder(context)
//        alertDialog.setTitle("提示")
//        alertDialog.setPositiveButton("确定",null)
//        val alertDialog1=alertDialog.create()
//        alertDialog1.window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
//        alertDialog1.show()
    }
}