package com.example.freenote

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_clock_list.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.lang.Exception
import kotlin.concurrent.thread

class clockList : AppCompatActivity() {
    private val clockList = ArrayList<inputClockOfShow>()
    lateinit var adapter:clockAdapter
    var username = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_list)
        setSupportActionBar(toolbarOfClockList)
        supportActionBar?.let{
            it.setDisplayHomeAsUpEnabled(true)
        }
        val alarmManager:AlarmManager=getSystemService(Context.ALARM_SERVICE) as AlarmManager
        username=intent.getStringExtra("userName").toString()
        initClocks()
        val layoutManager= GridLayoutManager(this,1)
        recyclerView.layoutManager=layoutManager
        adapter= clockAdapter(this,clockList ,"people",alarmManager)
        recyclerView.adapter=adapter
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
    private  fun initClocks(){
        clockList.clear()
        thread {
            try {
                val client= OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("userName", username)
                    .build()
                val request= Request.Builder()
                    .url("http://175.178.189.121:8089/clockList")
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
}