package com.example.freenote

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_clock_list_of_note.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class ClockListOfNote : AppCompatActivity() {
    private val clockList = ArrayList<inputClockOfShow>()
//    private val alarmManager:AlarmManager=getSystemService(Context.ALARM_SERVICE) as AlarmManager
//    lateinit var adapter = clockAdapter(this, clockList,"note",alarmManager)
    lateinit var adapter:clockAdapter
    private var noteTitle=" "
    private var userName = " "
    private var owner = " "
    private var noteId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_list_of_note)
        setSupportActionBar(toolbarOfClockListOfNote)
        supportActionBar?.let{
            it.setDisplayHomeAsUpEnabled(true)
        }
        noteTitle = intent.getStringExtra("noteTitle").toString()
        userName = intent.getStringExtra("userName").toString()
        owner = intent.getStringExtra("owner").toString()
        Log.d("11111",owner)
        val noteTime = intent.getStringExtra("noteTime")
        val cancel = intent.getStringExtra("cancel")
        noteId = intent.getIntExtra("noteId",0)
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val d1 = df.parse(noteTime.toString())
        val d2 = Date(System.currentTimeMillis())
        val diff = d1.time - d2.time
        if(owner!=userName||diff<=0||cancel=="1"){
            addClock.isEnabled=false
            addClock.isVisible=false
        }
        initClocks()
        val layoutManager= GridLayoutManager(this,1)
        recyclerView.layoutManager=layoutManager
        val alarmManager:AlarmManager=getSystemService(Context.ALARM_SERVICE) as AlarmManager
        adapter= clockAdapter(this,clockList ,"note",alarmManager)
        recyclerView.adapter=adapter

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            refreshClocks()
        }

        addClock.setOnClickListener {

            AlertDialog.Builder(this).apply {
                setTitle("是否创建共享提醒")
                setCancelable(true)
                setNegativeButton("取消",null)
                setPositiveButton("创建") { _, _ ->
                    val currentTime = Calendar.getInstance()
                    val c = Calendar.getInstance()
                    c.setTimeInMillis(System.currentTimeMillis())

                    TimePickerDialog(this@ClockListOfNote, 0, TimePickerDialog.OnTimeSetListener {
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
                                    showResponse(responseData,c)
                                }
                            }catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }

                        }
                    }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), false).show()

                    DatePickerDialog(this@ClockListOfNote,0, DatePickerDialog.OnDateSetListener{
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.clock_list_toolbar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->finish()
        }
        return true
    }

    private fun refreshClocks(){
        initClocks()
        swipeRefresh.isRefreshing=false
    }

    private  fun initClocks(){
        clockList.clear()
        thread {
            try {
                val client= OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("noteId", noteId.toString())
                    .build()
                val request= Request.Builder()
                    .url("http://10.0.2.2:8089/clockListOfNote")
                    .post(requestBody)
                    .build()
                val response=client.newCall(request).execute()
                val responseData=response.body?.string()
                if ( responseData != null ){
                    parseJSONWithJSONObject(responseData)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
    private fun parseJSONWithJSONObject(jsonData: String){
        runOnUiThread {
            try{
                val jsonArray= JSONArray(jsonData)
                for(i in 0 until jsonArray.length()){
                    val jsonObject = jsonArray.getJSONObject(i)
                    var inputClockOfShow=inputClockOfShow(jsonObject.getInt("Id"),jsonObject.getInt("NoteId"),jsonObject.getString("Name"),jsonObject.getString("Title"),jsonObject.getString("Time"),jsonObject.getString("Set"))
                    clockList.add(inputClockOfShow)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun showResponse(response: String,c:Calendar)
    {
        runOnUiThread{
            if(response=="服务器出现问题，请重试" ||response=="该闹钟已经存在，请勿重复创建"){
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
            }else{
                Log.d("1111",owner)
                val intent= Intent(this@ClockListOfNote,AlarmReceiver::class.java)
                //下面的msg内容要改成记录的内容
                intent.putExtra("title",noteTitle)
                intent.putExtra("userName",userName)
                intent.putExtra("owner",owner)
                //requestCode要改成该记录的id，不然闹钟取消不掉
                val pendingIntent= PendingIntent.getBroadcast(this@ClockListOfNote,response.toInt(),intent,0)
                val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE)as AlarmManager
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent)
            }
        }
    }
}