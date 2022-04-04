package com.example.freenote

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_note.*
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import java.text.SimpleDateFormat


class addNote : AppCompatActivity() {

    private val friendList = ArrayList<CharSequence>()
    var userName = String()
    var type = "会议"
    var time1=""
    private val choose = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        setSupportActionBar(toolbarOfAdd)
        supportActionBar?.let{
            it.setDisplayHomeAsUpEnabled(true)
        }
        friendName.isVisible=false
        friendName.isEnabled=false
        val missingOfNoteType=intent.getStringExtra("noteType")
        time1 = time.text.toString()
        userName = intent.getStringExtra("userName").toString()
        val spinnerOfNoteType=findViewById<Spinner>(R.id.noteType)
        val array:Array<String> = arrayOf("会议","出行")
        val adapter:ArrayAdapter<String> = ArrayAdapter(this,R.layout.spinner_item,array)
        spinnerOfNoteType.adapter=adapter
        spinnerOfNoteType.setSelection(missingOfNoteType.toString().toInt())
        spinnerOfNoteType.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                when(position){
                    0-> {
                        type="会议"
                    }
                    1->type="出行"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
               TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        initFriends()

        share.setOnClickListener {
            AlertDialog.Builder(this).apply{
                setTitle("添加参与者")
                choose.clear()
                setMultiChoiceItems(friendList.toTypedArray(),null,DialogInterface.OnMultiChoiceClickListener() { _, which, _ ->
                    choose.add(friendList[which].toString())
                })
                setPositiveButton("添加"){_,_->
                    friendName.text="参加者：\n"+choose.toString()
                    friendName.isVisible=true
                    friendName.isEnabled=true
                }
                setCancelable(true)
                show()
            }
        }

        time.setOnClickListener{
            val currentTime = Calendar.getInstance()
            val c = Calendar.getInstance()
            c.setTimeInMillis(System.currentTimeMillis())
            TimePickerDialog(this@addNote, 0, TimePickerDialog.OnTimeSetListener {
                    _, hourOfDay, minute ->
                //设置当前时间
                // 根据用户选择的时间来设置Calendar对象
                c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                c.set(Calendar.MINUTE, minute)
                c.set(Calendar.SECOND,0)
                c.set(Calendar.MILLISECOND,0)
                val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
                time.text=df.format(c.time)
            }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), false).show()

            DatePickerDialog(this@addNote,0, DatePickerDialog.OnDateSetListener{
                    _,year,month,dayOfMonth->
                c.set(Calendar.YEAR,year)
                c.set(Calendar.MONTH,month)
                c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            },currentTime.get(Calendar.YEAR),currentTime.get(Calendar.MONTH),currentTime.get(
                Calendar.DAY_OF_MONTH)).show()

        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_toolbar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.done->{
                if(time.text.toString()==time1){
                    Toast.makeText(this@addNote , "时间不得为空", Toast.LENGTH_SHORT).show()
                    return true
                }
                val gson= Gson()
                var note=Note(userName,noteTitle.text.toString(),time.text.toString(),area.text.toString(),detail.text.toString(),choose.toTypedArray(),type)
                val message = gson.toJson(note)
                val requestBody = message.toRequestBody()
                thread {
                    try {
                        val client = OkHttpClient()
                        val request = Request.Builder()
                            .url(("http://10.0.2.2:8089/addNote"))
                            .post(requestBody)
                            .build()
                        val response = client.newCall(request).execute()
                        val responseData = response.body?.string()
                        if (responseData!=null)
                        {
                            showResponse(responseData)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                    val intent = Intent(this@addNote, MainActivity::class.java)
                    intent.putExtra("userName", userName)
                    startActivity(intent)
                    finish()
                }
            }
            android.R.id.home->{
                val intent = Intent(this@addNote, MainActivity::class.java)
                intent.putExtra("userName", userName)
                startActivity(intent)
                finish()
            }
        }
        return true
    }
    private  fun showResponse(response: String){
        runOnUiThread{
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }
    }
    private  fun initFriends(){
        friendList.clear()
        thread {
            try {
                val client= OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("userName", userName)
                    .build()
                val request= Request.Builder()
                    .url("http://10.0.2.2:8089/friendList")
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
        try{
            val jsonArray= JSONArray(jsonData)
            for(i in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(i)
                val friendName = jsonObject.getString("FriendName")
                runOnUiThread{
                    friendList.add(Friend(friendName).username)
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}
