package com.example.freenote

import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class clock : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)
        val mediaPlayer  = MediaPlayer.create(this,R.raw.tishi)
        mediaPlayer.start()
        AlertDialog.Builder(this@clock).setTitle("闹钟").setMessage(intent.getStringExtra("msg"))
            .setPositiveButton("关闭闹铃", DialogInterface.OnClickListener { _, _ ->
                mediaPlayer.stop()
                this@clock.finish()
            }).show()

    }
}
