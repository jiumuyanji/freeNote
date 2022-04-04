package com.example.freenote

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_sign.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.Exception
import kotlin.concurrent.thread

class sign : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        val type=intent.getStringExtra("type")
        register.isVisible=false
        register.isEnabled=false
        editText4.isEnabled=false
        editText4.isVisible=false
        if (type=="0") {
            Toast.makeText(this, "点击register注册", Toast.LENGTH_SHORT).show()
            register.isEnabled=true
            register.isVisible=true
            editText4.isEnabled=true
            editText4.isVisible=true
            begin.isEnabled=false
            begin.isVisible=false
            signUp.isEnabled=false
            signUp.isVisible=false
        }
        begin.setOnClickListener {
            val account2=editText2.text.toString()
            val password2=editText3.text.toString()
            if (account2=="zzt"&&password2=="zzt")
            {
                val intent = Intent(this@sign, MainActivity::class.java)
                intent.putExtra("userName", "zzt")
                startActivity(intent)
                finish()
            }
            if (account2== ""&& password2=="") {
                Toast.makeText(this, "请输入用户名密码", Toast.LENGTH_SHORT).show()
            }
            else {
                thread {
                    try {
                        val client = OkHttpClient()
                        val requestBody = FormBody.Builder()
                            .add("username", account2)
                            .add("password", password2)
                            .build()
                        val request = Request.Builder()
                            .url("http://10.0.2.2:8089/signIn")
                            .post(requestBody)
                            .build()
                        val response = client.newCall(request).execute()
                        val responseData = response.body?.string()
                        if (responseData != null) {
                            showResponse(responseData,account2)
                        }
//                        val client = OkHttpClient()
//                        val request = Request.Builder()
//                            .url("http://10.0.2.2:8089/")
//                            .build()
//                        val response = client.newCall(request).execute()
//                        val responseData = response.body?.string()
//                        if (responseData != null) {
//                            showResponse(responseData)
//                        }
                    }catch (e:Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        register.setOnClickListener{
            val account2=editText2.text.toString()
            val password2=editText3.text.toString()
            val code2=editText4.text.toString()
            if (account2== ""||password2==""||code2=="") {
                Toast.makeText(this, "请输入用户名密码验证码", Toast.LENGTH_SHORT).show()
            }
            else {
                thread {
                    try {
                        val client = OkHttpClient()
                        val requestBody = FormBody.Builder()
                            .add("username", account2)
                            .add("password", password2)
                            .add("code",code2)
                            .build()
                        val request = Request.Builder()
                            .url("http://10.0.2.2:8089/signUp")
                            .post(requestBody)
                            .build()
                        val response = client.newCall(request).execute()
                        val responseData = response.body?.string()
                        if (responseData != null) {
                            showResponse2(responseData,account2)
                        }
                    }catch (e:Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        signUp.setOnClickListener {
            val intent = Intent(this@sign, sign::class.java)
            intent.putExtra("type", "0")
            startActivity(intent)
            finish()
        }
    }
    private fun showResponse(response: String,userName: String)
    {
        runOnUiThread{
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
            if(response=="密码正确"){
                val intent = Intent(this@sign, MainActivity::class.java)
                intent.putExtra("userName", userName)
                startActivity(intent)
                finish()
            }
        }
    }
    private fun showResponse2(response: String,userName: String)
    {
        runOnUiThread{
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
            if(response=="注册成功"){
                val intent = Intent(this@sign, MainActivity::class.java)
                intent.putExtra("userName", userName)
                startActivity(intent)
                finish()
            }
        }
    }
}
