package com.example.freenote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_friend_list.*
import kotlinx.android.synthetic.main.activity_friend_list.recyclerView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class FriendList : AppCompatActivity() {

    private val friendList = ArrayList<Friend>()
    private var adapter=FriendAdapter(this,friendList)
    var username = String()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)
        setSupportActionBar(toolbarOfFriendList)
        supportActionBar?.let{
            it.setDisplayHomeAsUpEnabled(true)
        }
        username=intent.getStringExtra("userName").toString()
        initFriends()
        val layoutManager=GridLayoutManager(this,1)
        recyclerView.layoutManager=layoutManager
        adapter=FriendAdapter(this,friendList)
        recyclerView.adapter=adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.friend_list_toolbar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add->{
                val intent=Intent(this@FriendList, addFriend::class.java)
                intent.putExtra("userName",username)
                startActivity(intent)
            }
            android.R.id.home->finish()
        }
        return true
    }
    private  fun initFriends(){
        friendList.clear()
        thread {
            try {
                val client=OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("userName", username)
                    .build()
                val request=Request.Builder()
                    .url("http://10.0.2.2:8089/friendList")
                    .post(requestBody)
                    .build()
                val response=client.newCall(request).execute()
                val responseData=response.body?.string()
                if ( responseData != null ){
                    parseJSONWithJSONObject(responseData)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    private fun parseJSONWithJSONObject(jsonData: String){
        runOnUiThread {
            try{
                val jsonArray=JSONArray(jsonData)
                for(i in 0 until jsonArray.length()){
                    val jsonObject = jsonArray.getJSONObject(i)
                    val friendName = jsonObject.getString("FriendName")
                    friendList.add(Friend(friendName))
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
            adapter.notifyDataSetChanged()
        }
    }
}
