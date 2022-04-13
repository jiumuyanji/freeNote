package com.example.freenote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_friend.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception
import kotlin.concurrent.thread

class addFriend : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
        val userName= intent.getStringExtra("userName").toString()
        back.setOnClickListener {
            finish()
        }
        begin.setOnClickListener {
            val friendName=friendName.text.toString()
            if(friendName == userName){
                Toast.makeText(this, "请勿添加自己为好友", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val code=code.text.toString()
            if (friendName== ""&& code=="") {
                Toast.makeText(this, "请输入好友名和验证码", Toast.LENGTH_SHORT).show()
            }else {
                thread {
                    try {
                        val client = OkHttpClient()
                        val requestBody = FormBody.Builder()
                            .add("userName", userName)
                            .add("friendName", friendName)
                            .add("code",code)
                            .build()
                        val request = Request.Builder()
                            .url("http://175.178.189.121:8089/addFriend")
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
                }
            }
        }
    }
    private fun showResponse(response: String)
    {
        runOnUiThread{
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }
    }
}
