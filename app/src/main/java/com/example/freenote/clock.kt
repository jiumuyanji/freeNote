package com.example.freenote

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class clock : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)
        val userName = intent.getStringExtra("userName")
        val owner = intent.getStringExtra("owner")
        Log.d("1111111",owner.toString())
        val mediaPlayer  = MediaPlayer.create(this,R.raw.tishi)
        mediaPlayer.start()
        AlertDialog.Builder(this@clock).setCancelable(false).setTitle(intent.getStringExtra("title")).setMessage("发起者： "+userName.toString())
            .setPositiveButton("关闭闹铃", DialogInterface.OnClickListener { _, _ ->
                mediaPlayer.stop()
                val intent = Intent(this@clock, MainActivity::class.java)
                intent.putExtra("userName", owner)
                startActivity(intent)
                this@clock.finish()
            }).show()
    }
}
