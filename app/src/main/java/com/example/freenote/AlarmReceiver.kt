package com.example.freenote

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.view.Window
import android.view.WindowManager
import android.widget.Toast

class AlarmReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent){
        Toast.makeText(context , "闹钟", Toast.LENGTH_LONG).show()
        val data=intent.getStringExtra("msg")
        val intent1=Intent(context,clock::class.java)
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent1.putExtra("msg",data)
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