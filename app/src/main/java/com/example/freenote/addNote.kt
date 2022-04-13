package com.example.freenote

import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
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
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.nav_header.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat


class addNote : AppCompatActivity() {

    private val friendList = ArrayList<CharSequence>()
    private val picturesList = ArrayList<Bitmap>()
    private var pAdapter=PictureAdapter(this,picturesList)
    private val takePhoto=1
    private val fromAlbum=2
    lateinit var imageUri:Uri
    lateinit var outputImage:File
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
        recyclerView.isVisible=false
        recyclerView.isEnabled=false
        val missingOfNoteType=intent.getStringExtra("noteType")
        time1 = time.text.toString()
        userName = intent.getStringExtra("userName").toString()
        val array:Array<String> = arrayOf("会议", "出差","旅游","聚会","购物","接送","见面","其他")
        val adapter:ArrayAdapter<String> = ArrayAdapter(this,R.layout.spinner_item,array)
        noteType.adapter=adapter
        noteType.setSelection(missingOfNoteType.toString().toInt())
        noteType.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                when(position){
                    0-> type="会议"
                    1-> type="出差"
                    2-> type="旅游"
                    3-> type="聚会"
                    4-> type="购物"
                    5-> type="接送"
                    6-> type="见面"
                    7-> type="其他"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
               TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        initFriends()
        val layoutManager= GridLayoutManager(this,2)
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter=pAdapter

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

        addPictures.setOnClickListener {
            AlertDialog.Builder(this@addNote).apply {
                val wayOfGetPictureTypes: Array<CharSequence> = arrayOf( "使用相机拍照","从相册中选择图片")
                var type = 0
                setCancelable(true)
                setSingleChoiceItems(wayOfGetPictureTypes, 0) { _, which ->
                    type = which
                }
                setPositiveButton("ok") { _, _ ->
                    recyclerView.isEnabled=true
                    recyclerView.isVisible=true
                    when(type){
                        0->{
                            outputImage= File(externalCacheDir,"output_image.jpg")
                            if(outputImage.exists()){
                                outputImage.delete()
                            }
                            outputImage.createNewFile()
                            imageUri=if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
                                FileProvider.getUriForFile(this@addNote,"com.example.cameraalbumtest.fileprovider",outputImage)
                            }else{
                                Uri.fromFile(outputImage)
                            }
                            val intent=Intent("android.media.action.IMAGE_CAPTURE")
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
                            startActivityForResult(intent,1)
                        }
                        1->{
                            val intent=Intent(Intent.ACTION_OPEN_DOCUMENT)
                            intent.addCategory(Intent.CATEGORY_OPENABLE)
                            intent.type="image/*"
                            startActivityForResult(intent,2)
                        }
                    }
                }
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
                var note=Note(userName,noteTitle.text.toString(),time.text.toString(),area.text.toString(),detail.text.toString(),choose.toTypedArray(),type,picturesList.size)
                val message = gson.toJson(note)
                val requestBody = message.toRequestBody()
                thread {
                    try {
                        val client = OkHttpClient()
                        val request = Request.Builder()
                            .url(("http://175.178.189.121:8089/addNote"))
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
            if(response=="服务器出现问题，请重试"||response=="存在同名备忘录,请修改")
            {
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
            }else{
                if(picturesList.size!=0){
                    thread {
                        try {
                            val client = OkHttpClient()
                            var byteArrayOutputStream = ByteArrayOutputStream()
                            val requestBody2 = MultipartBody.Builder()
                            requestBody2.setType(MultipartBody.FORM)
                            requestBody2.addFormDataPart("userName",userName)
                            requestBody2.addFormDataPart("noteTitle",noteTitle.text.toString())
                            requestBody2.addFormDataPart("size",picturesList.size.toString())
                            var n=0
                            for(i in picturesList){
                                byteArrayOutputStream.reset()
                                i.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream)
                                var byteArray = byteArrayOutputStream.toByteArray()
                                requestBody2.addFormDataPart("picture"+n.toString(),"picture"+n.toString()+".png",byteArray.toRequestBody("multipart/form-data".toMediaTypeOrNull(), 0, byteArray.size))
                                n++
                            }
                            val requestBody=requestBody2.build()
                            val request = Request.Builder()
                                .url("http://175.178.189.121:8089/setImage2")
                                .post(requestBody)
                                .build()
                            val response = client.newCall(request).execute()
                            val responseData = response.body?.string()
                            if (responseData != null) {
                                showResponse3(responseData)
                            }
                        }catch (e:Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
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
                    .url("http://175.178.189.121:8089/friendList")
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            takePhoto->{
                if(resultCode== Activity.RESULT_OK){
                    val bitmap= BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    picturesList.add(bitmap)
                    pAdapter.notifyDataSetChanged()
                }
            }
            fromAlbum->{
                if(resultCode== Activity.RESULT_OK && data!=null){
                    data.data?.let {
                            uri ->
                        val bitmap=getBitmapFromUri(uri)
                        if(bitmap != null){
                            picturesList.add(bitmap)
                            pAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    //当头像需要修改大小的时候
    private fun rotateIfRequired(bitmap: Bitmap):Bitmap{
        val exif= ExifInterface(outputImage.path)
        val orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when(orientation){
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap,90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap,180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap,270)
            else->bitmap
        }
    }
    private fun rotateBitmap(bitmap: Bitmap,degree:Int):Bitmap{
        val matrix= Matrix()
        matrix.postRotate(degree.toFloat())
        val rotateBitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
        bitmap.recycle()
        return rotateBitmap
    }

    private fun getBitmapFromUri(uri: Uri)=contentResolver.openFileDescriptor(uri,"r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }
    private fun showResponse3(response: String)
    {
        runOnUiThread {
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }
    }
}
