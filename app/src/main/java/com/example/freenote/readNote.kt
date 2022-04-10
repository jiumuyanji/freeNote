package com.example.freenote


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.gson.Gson
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

    private var noteTitle=" "
    private var userName = " "
    private var owner = " "
    private var noteId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_note)
        noteId = intent.getIntExtra("noteId",0)
        val noteTime = intent.getStringExtra("noteTime")
        val noteArea = intent.getStringExtra("noteArea")
        val noteDetail = intent.getStringExtra("noteDetail")
        val friendList = intent.getStringExtra("friendList")
        val type = intent.getStringExtra("noteType")
        val cancel = intent.getStringExtra("cancel")
        noteTitle = intent.getStringExtra("noteTitle").toString()
        userName = intent.getStringExtra("userName").toString()
        owner = intent.getStringExtra("owner").toString()
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val d1 = df.parse(noteTime.toString())
        val d2 = Date(System.currentTimeMillis())
        val diff = d1.time-d2.time
        if(owner == userName&& diff>=0 &&cancel=="0"){
            note.isClickable=true
            NoteArea.isClickable=true
            NoteTime.isClickable=true
        }
        setSupportActionBar(readNoteToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        readNoteCollapsingBar.title = noteTitle
        if(cancel == "1"){
            readNoteCollapsingBar.title = noteTitle+"(已取消！)"
        }
        if(type=="会议"){
            Glide.with(this).load(R.drawable.meeting).into(noteImageView)
        }
        else if(type=="出差"){
            Glide.with(this).load(R.drawable.business).into(noteImageView)
        }
        else if(type=="旅游"){
            Glide.with(this).load(R.drawable.trip).into(noteImageView)
        }
        else if(type=="聚会"){
            Glide.with(this).load(R.drawable.meeting2).into(noteImageView)
        }
        else if(type=="购物"){
            Glide.with(this).load(R.drawable.shopping).into(noteImageView)
        }
        else if(type=="接送"){
            Glide.with(this).load(R.drawable.huoche).into(noteImageView)
        }
        else if(type=="见面"){
            Glide.with(this).load(R.drawable.meet).into(noteImageView)
        }
        else{
            Glide.with(this).load(R.drawable.papers).into(noteImageView)
        }

        note.text = noteDetail
        NoteTime.text = "预计时间：  " + noteTime
        NoteArea.text = "预定地点：  " + noteArea
        userName3.text = "发布者：  " + userName.toString()
        friendList1.text = "参与者：  " + friendList.toString()

        if(owner!=userName){
            delete.isEnabled=false
            delete.isVisible=false
        }

        initLeaveMessage()

        note.setOnClickListener {
            AlertDialog.Builder(this).apply {
                val view =LayoutInflater.from(this@readNote).inflate(R.layout.dialog,null)
                val change:EditText = view.findViewById(R.id.change)
                setTitle("修改事务详细内容")
                setView(view)
                change.setText(noteDetail)
                setCancelable(true)
                setPositiveButton("确定"){_,_->
                    note.text=change.text.toString()
                    val gson= Gson()
                    val c=change (noteId,change.text.toString())
                    val message = gson.toJson(c)
                    val requestBody = message.toRequestBody()
                    thread {
                        try {
                            val client = OkHttpClient()
                            val request = Request.Builder()
                                .url(("http://10.0.2.2:8089/changeNote"))
                                .post(requestBody)
                                .build()
                            val response = client.newCall(request).execute()
                            val responseData = response.body?.string()
                            if (responseData!=null)
                            {
                                showResponse2(responseData)
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }
                setNegativeButton("取消",null)
                show()
            }
        }

        NoteArea.setOnClickListener {
            AlertDialog.Builder(this).apply {
                val view =LayoutInflater.from(this@readNote).inflate(R.layout.dialog,null)
                val change:EditText = view.findViewById(R.id.change)
                setTitle("修改预定地址内容")
                setView(view)
                change.setText(noteArea)
                setCancelable(true)
                setPositiveButton("确定"){_,_->
                    NoteArea.text="预定地点：  "+change.text.toString()
                    val gson= Gson()
                    val c=change (noteId,change.text.toString())
                    val message = gson.toJson(c)
                    val requestBody = message.toRequestBody()
                    thread {
                        try {
                            val client = OkHttpClient()
                            val request = Request.Builder()
                                .url(("http://10.0.2.2:8089/changeArea"))
                                .post(requestBody)
                                .build()
                            val response = client.newCall(request).execute()
                            val responseData = response.body?.string()
                            if (responseData!=null)
                            {
                                showResponse2(responseData)
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }
                setNegativeButton("取消",null)
                show()
            }
        }

        leaveMessage.setOnClickListener {
            AlertDialog.Builder(this).apply {
                val view =LayoutInflater.from(this@readNote).inflate(R.layout.dialog,null)
                val change:EditText = view.findViewById(R.id.change)
                setTitle("发表留言")
                setView(view)
                setCancelable(true)
                setPositiveButton("确定"){_,_->
                    val lm=owner+":"+change.text.toString()+"\n"
                    leaveMessage.text=leaveMessage.text.toString()+lm
                    val gson= Gson()
                    val c=change (noteId,lm)
                    val message = gson.toJson(c)
                    val requestBody = message.toRequestBody()
                    thread {
                        try {
                            val client = OkHttpClient()
                            val request = Request.Builder()
                                .url(("http://10.0.2.2:8089/addLeaveMessage"))
                                .post(requestBody)
                                .build()
                            val response = client.newCall(request).execute()
                            val responseData = response.body?.string()
                            if (responseData!=null)
                            {
                                showResponse2(responseData)
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }
                setNegativeButton("取消",null)
                show()
            }
        }

        NoteTime.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("修改预计时间内容")
                setCancelable(true)
                setPositiveButton("确定"){_,_->
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
                        val time1 = df.format(c.time)
                        NoteTime.text="预定地点：  "+time1
                        val gson= Gson()
                        val change=change (noteId,time1)
                        val message = gson.toJson(change)
                        val requestBody = message.toRequestBody()
                        thread {
                            try {
                                val client = OkHttpClient()
                                val request = Request.Builder()
                                    .url(("http://10.0.2.2:8089/changeTime"))
                                    .post(requestBody)
                                    .build()
                                val response = client.newCall(request).execute()
                                val responseData = response.body?.string()
                                if (responseData!=null)
                                {
                                    showResponse2(responseData)
                                }
                            }catch (e:Exception){
                                e.printStackTrace()
                            }
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
                setNegativeButton("取消",null)
                show()
            }
        }


        delete.setOnClickListener {
            if (diff >= 0&&cancel=="0") {
                AlertDialog.Builder(this).apply {
                    setTitle("是否取消改待办")
                    setCancelable(true)
                    setNegativeButton("返回",null)
                    setPositiveButton("确定"){_, _->
                        thread {
                            try {
                                val client = OkHttpClient()
                                val requestBody = FormBody.Builder()
                                    .add("userName", userName.toString())
                                    .add("title", noteTitle.toString())
                                    .build()
                                val request = Request.Builder()
                                    .url("http://10.0.2.2:8089/cancel")
                                    .post(requestBody)
                                    .build()
                                val response = client.newCall(request).execute()
                                val responseData = response.body?.string()
                                if (responseData != null) {
                                    showResponse2(responseData)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                    show()
                }
            }
            else if(diff>=0&&cancel=="1"){
                Toast.makeText(this,"该事务已取消，请等过期后再删除", Toast.LENGTH_SHORT).show()
            }
            else {
                AlertDialog.Builder(this).apply {
                    setTitle("是否删除事务")
                    setCancelable(true)
                    setNegativeButton("返回", null)
                    setPositiveButton("确定") { _, _ ->
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
                                    showResponse2(responseData)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        finish()
                    }
                    show()
                }
            }
        }


        clock.setOnClickListener {
            val intent = Intent(this@readNote, ClockListOfNote::class.java)
            intent.putExtra("noteId",noteId)
            intent.putExtra("noteTitle",noteTitle)
            intent.putExtra("userName",userName)
            intent.putExtra("owner",owner)
            intent.putExtra("cancel",cancel)
            intent.putExtra("noteTime",noteTime)
            startActivity(intent)

//            AlertDialog.Builder(this).apply {
//                setTitle("是否创建共享提醒")
//                setCancelable(true)
//                setNegativeButton("取消",null)
//                setPositiveButton("创建") { _, _ ->
//                    val currentTime = Calendar.getInstance()
//                    val c = Calendar.getInstance()
//                    c.setTimeInMillis(System.currentTimeMillis())
//
//                    TimePickerDialog(this@readNote, 0, TimePickerDialog.OnTimeSetListener {
//                            _, hourOfDay, minute ->
//                        //设置当前时间
//                        // 根据用户选择的时间来设置Calendar对象
//                        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
//                        c.set(Calendar.MINUTE, minute)
//                        c.set(Calendar.SECOND,0)
//                        c.set(Calendar.MILLISECOND,0)
//                        val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
//                        val time=df.format(c.time)
//                        thread {
//                            try {
//                                val client = OkHttpClient()
//                                val requestBody = FormBody.Builder()
//                                    .add("userName", userName.toString())
//                                    .add("title", noteTitle.toString())
//                                    .add("time",time)
//                                    .build()
//                                val request = Request.Builder()
//                                    .url("http://10.0.2.2:8089/addClock")
//                                    .post(requestBody)
//                                    .build()
//                                val response = client.newCall(request).execute()
//                                val responseData = response.body?.string()
//                                if (responseData != null) {
//                                    showResponse(responseData,c)
//                                }
//                            }catch (e: Exception) {
//                                e.printStackTrace()
//                            }
//
//                        }
//                    }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), false).show()
//
//                    DatePickerDialog(this@readNote,0, DatePickerDialog.OnDateSetListener{
//                            _,year,month,dayOfMonth->
//                        c.set(Calendar.YEAR,year)
//                        c.set(Calendar.MONTH,month)
//                        c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
//                    },currentTime.get(Calendar.YEAR),currentTime.get(Calendar.MONTH),currentTime.get(
//                        Calendar.DAY_OF_MONTH)).show()
//
//                }
//                show()
//            }
        }
    }

    private fun initLeaveMessage(){
        leaveMessage.setText("留言区：\n")
        thread {
            try {
                val client = OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("noteId",noteId.toString())
                    .build()
                val request = Request.Builder()
                    .url("http://10.0.2.2:8089/getLeaveMessage")
                    .post(requestBody)
                    .build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                if (responseData != null) {
                    showResponseOfLeaveMessage(responseData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showResponse2(response: String)
    {
        runOnUiThread{
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showResponseOfLeaveMessage(response: String)
    {
        runOnUiThread{
            leaveMessage.text=leaveMessage.text.toString()+response
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
