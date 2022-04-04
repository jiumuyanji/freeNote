package com.example.freenote

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.activity_read_note.*
import kotlinx.android.synthetic.main.activity_read_note.clock
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class readNote : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_note)
        val noteTitle = intent.getStringExtra("noteTitle")
        val noteTime = intent.getStringExtra("noteTime")
        val noteArea = intent.getStringExtra("noteArea")
        val noteDetail = intent.getStringExtra("noteDetail")
        val userName = intent.getStringExtra("userName")
        val friendList = intent.getStringExtra("friendList")
        setSupportActionBar(readNoteToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        readNoteCollapsingBar.title = noteTitle
        Glide.with(this).load(R.drawable.paper5).into(noteImageView)
        note.text = noteDetail
        NoteTime.text = "预计时间：  " + noteTime
        NoteArea.text = "预定地点：  " + noteArea
        userName3.text = "发布者：" + userName.toString()
        friendList1.text = "参与者：" + friendList.toString()

        delete.setOnClickListener {
            thread {
                try {
                    val client = OkHttpClient()
                    val requestBody = FormBody.Builder()
                        .add("userName", userName.toString())
                        .add("title", noteTitle.toString())
                        .build()
                    val request = Request.Builder()
                        .url("http://10.0.2.2:8089/delete")
                        .post(requestBody)
                        .build()
                    val response = client.newCall(request).execute()
                    val responseData = response.body?.string()
                    if (responseData != null) {
                        showResponse(responseData)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            finish()
        }

        clock.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("是否创建共享提醒")
                setCancelable(true)
                setNegativeButton("取消",null)
                setPositiveButton("创建") { _, _ ->
                    val currentTime = Calendar.getInstance()
                    val c = Calendar.getInstance()
                    c.setTimeInMillis(System.currentTimeMillis())
                    TimePickerDialog(this@readNote, 0, TimePickerDialog.OnTimeSetListener {
                            _, hourOfDay, minute ->
                        //设置当前时间
                        // 根据用户选择的时间来设置Calendar对象
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        c.set(Calendar.MINUTE, minute)
                        c.set(Calendar.SECOND,0)
                        c.set(Calendar.MILLISECOND,0)
                        val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
                        val time=df.format(c.time)
                        thread {
                            try {
                                val client = OkHttpClient()
                                val requestBody = FormBody.Builder()
                                    .add("userName", userName.toString())
                                    .add("title", noteTitle.toString())
                                    .add("time",time)
                                    .build()
                                val request = Request.Builder()
                                    .url("http://10.0.2.2:8089/addClock")
                                    .post(requestBody)
                                    .build()
                                val response = client.newCall(request).execute()
                                val responseData = response.body?.string()
                                if (responseData != null) {
                                    showResponse(responseData)
                                }
                            }catch (e: Exception) {
                                e.printStackTrace()
                            }
                            val intent = Intent(this@readNote, clockList::class.java)
                            intent.putExtra("userName", userName)
                            startActivity(intent)
                            finish()
                        }
                    }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), false).show()

                    DatePickerDialog(this@readNote,0, DatePickerDialog.OnDateSetListener{
                            _,year,month,dayOfMonth->
                        c.set(Calendar.YEAR,year)
                        c.set(Calendar.MONTH,month)
                        c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                    },currentTime.get(Calendar.YEAR),currentTime.get(Calendar.MONTH),currentTime.get(
                        Calendar.DAY_OF_MONTH)).show()

                }
                show()
            }
        }
    }

    private fun showResponse(response: String)
    {
        runOnUiThread{
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
