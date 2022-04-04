package com.example.freenote


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.text_enter_view.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    //头像导入方式的选项
    private val takePhoto=1
    private val fromAlbum=2
    //头像保存的文件名
    private val path="iconImage"

    private var username = ""


    lateinit var imageUri:Uri
    lateinit var outputImage:File

    //recycleview的列表
    private val noteList=ArrayList<Note2>()
    private var adapter=NoteAdapter(this, noteList)
    private var type="note_of_user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        username=intent.getStringExtra("userName").toString()
        //初始化标题栏
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        //初始化滑动菜单
        navView.setCheckedItem(R.id.navAccount)
        navView.setNavigationItemSelectedListener(object :NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem):Boolean{
                when(item.itemId){
                    //跳转到账户管理界面，下面要改
                    R.id.navAccount->{
                        AlertDialog.Builder(this@MainActivity).apply {
                            setTitle("账号管理")
                            setPositiveButton("登录"){_, _ ->
                                val intent = Intent(this@MainActivity, sign::class.java)
                                intent.putExtra("type", "1")
                                startActivity(intent)
                                finish()
                            }
                            setNegativeButton("注册"){_,_ ->
                                val intent = Intent(this@MainActivity, sign::class.java)
                                intent.putExtra("type", "0")
                                startActivity(intent)
                                finish()
                            }
                            setCancelable(true)
                            show()
                        }
                    }
                    R.id.navAddFriend->{
                        val intent=Intent(this@MainActivity, addFriend::class.java)
                        intent.putExtra("userName",username)
                        startActivity(intent)
                    }
                    R.id.navFriendList->{
                        val intent=Intent(this@MainActivity, FriendList::class.java)
                        intent.putExtra("userName",username)
                        startActivity(intent)
                    }
                    //设置头像功能
                    R.id.navIcon->{
                        AlertDialog.Builder(this@MainActivity).apply {
                            val wayOfGetPictureTypes: Array<CharSequence> = arrayOf( "使用相机拍照","从相册中选择图片")
                            var type = 0
                            setCancelable(true)
                            setSingleChoiceItems(wayOfGetPictureTypes, 0) { _, which ->
                                type = which
                            }
                            setPositiveButton("ok") { _, _ ->
                                when(type){
                                    0->{
                                        outputImage=File(externalCacheDir,"output_image.jpg")
                                        if(outputImage.exists()){
                                            outputImage.delete()
                                        }
                                        outputImage.createNewFile()
                                        imageUri=if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                                            FileProvider.getUriForFile(this@MainActivity,"com.example.cameraalbumtest.fileprovider",outputImage)
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
                                        startActivityForResult(intent,fromAlbum)
                                    }
                                }
                            }
                            show()
                        }
                    }
                    R.id.navClockList->{
                        val intent=Intent(this@MainActivity, clockList::class.java)
                        intent.putExtra("userName",username)
                        startActivity(intent)
                    }
                }
                return true
            }
        })
        //设置滑动菜单的头像，要在这里打开Header，不能再layout那边打开，不然头像那个view为空
        val view=navView.inflateHeaderView(R.layout.nav_header)
        val f=File(filesDir.path+"/"+path)
        if(f.exists()) {
            val bytes=f.readBytes()
            val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            if(bitmap!=null)
            {
                val icon=view.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.iconImage)
                icon.setImageBitmap(bitmap)
            }
        }

        val layoutManager = GridLayoutManager(this, 1)
        recyclerView.layoutManager = layoutManager
        //初始化列表
        noteList.clear()
        initNotes1()

        adapter = NoteAdapter(this, noteList)
        recyclerView.adapter = adapter

        //下拉刷新列表
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            refreshNotes()
        }



        //添加功能 的悬浮按钮的监听
        fab.setOnClickListener {
            AlertDialog.Builder(this).apply {
                val noteTypes: Array<CharSequence> = arrayOf("会议", "出行")
                var type = 0
                setTitle("选择笔记类型")
                setCancelable(true)
                setSingleChoiceItems(noteTypes, 0) { _, which ->
                    type = which
                }
                setPositiveButton("ok") { _, _ ->
                    when(type){
                        0->{
                            val intent = Intent(this@MainActivity, addNote::class.java)
                            intent.putExtra("noteType", type.toString())
                            intent.putExtra("userName",username.toString())
                            startActivity(intent)
                            finish()
                        }
                        1->{
                            val intent = Intent(this@MainActivity, addNote::class.java)
                            intent.putExtra("noteType", type.toString())
                            intent.putExtra("userName",username.toString())
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                show()
            }
        }
    }

    //导入头像，从外面返回图片的处理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            takePhoto->{
                if(resultCode==Activity.RESULT_OK){
                    val bitmap=BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    iconImage.setImageBitmap(rotateIfRequired(bitmap))
//                    设置存储权限，data不需要
//                    if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
//                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
//                    } else{
//                        val localStream:FileInputStream=openFileInput(path)
//                        val bitmap = BitmapFactory.decodeStream(localStream)
//                        iconImage.setImageBitmap(bitmap)
//                        localStream.close()
//                    }
                    val file=File(filesDir.path+"/"+path)
                    val fos:FileOutputStream=file.outputStream()
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.flush()
                    fos.close()
                }
            }
            fromAlbum->{
                if(resultCode==Activity.RESULT_OK && data!=null){
                    data.data?.let {
                        uri ->val bitmap=getBitmapFromUri(uri)
                        iconImage.setImageBitmap(bitmap)
                        val file=File(filesDir.path+"/"+path)
                        val fos:FileOutputStream=file.outputStream()
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                        fos.flush()
                        fos.close()
                    }
                }
            }
        }
    }

    //当头像需要修改大小的时候
    private fun rotateIfRequired(bitmap: Bitmap):Bitmap{
        val exif=ExifInterface(outputImage.path)
        val orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
        return when(orientation){
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap,90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap,180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap,270)
            else->bitmap
        }
    }
    private fun rotateBitmap(bitmap: Bitmap,degree:Int):Bitmap{
        val matrix=Matrix()
        matrix.postRotate(degree.toFloat())
        val rotateBitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
        bitmap.recycle()
        return rotateBitmap
    }

    private fun getBitmapFromUri(uri: Uri)=contentResolver.openFileDescriptor(uri,"r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    //头像处理结束

//    请求存储权限，不过头像存在data文件夹里不需要请求权限
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when(requestCode){
//            1->{
//                if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                    val localStream:FileInputStream=openFileInput(path)
//                    val bitmap = BitmapFactory.decodeStream(localStream)
//                    iconImage.setImageBitmap(bitmap)
//                }else{
//                    Toast.makeText(this,"权限不足，获取头像失败",Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    //刷新列表的函数
    private fun refreshNotes(){
        if(type=="note_of_user"){
            initNotes1()
        }
        else if (type=="note_of_friend"){
            initNotes2()
        }
        else if(type=="note_of_user_finish"){
            initNotes4()
        }
        else if(type=="note_of_friend_finish"){
            initNotes5()
        }
        else{
            initNotes3()
        }
    }

    //初始化列表的函数，后面在这里导入数据显示
    private fun initNotes1(){
        noteList.clear()
        thread {
            try {
                val client= OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("userName", username)
                    .build()
                val request= Request.Builder()
                    .url("http://10.0.2.2:8089/noteListOfUser")
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

    private fun initNotes2(){
        noteList.clear()
        thread {
            try {
                val client= OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("friendName", username)
                    .build()
                val request= Request.Builder()
                    .url("http://10.0.2.2:8089/noteListOfFriend")
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

    private fun initNotes3(){
        noteList.clear()
        thread {
            try {
                val client= OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("userName", username)
                    .add("type",type)
                    .build()
                val request= Request.Builder()
                    .url("http://10.0.2.2:8089/noteListOfType")
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

    private fun initNotes4(){
        noteList.clear()
        thread {
            try {
                val client= OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("userName", username)
                    .build()
                val request= Request.Builder()
                    .url("http://10.0.2.2:8089/noteListOfUserFinish")
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

    private fun initNotes5(){
        noteList.clear()
        thread {
            try {
                val client= OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("friendName", username)
                    .build()
                val request= Request.Builder()
                    .url("http://10.0.2.2:8089/noteListOfFriendFinish")
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

    private fun parseJSONWithJSONObject(jsonData: String)
    {
        runOnUiThread {
            try{
                val jsonArray= JSONArray(jsonData)
                for(i in 0 until jsonArray.length()){
                    val jsonObject = jsonArray.getJSONObject(i)
                    var n=Note2(jsonObject.getString("UserName"),jsonObject.getString("Title"),jsonObject.getString("NoteTime"),jsonObject.getString("NoteArea"),jsonObject.getString("Detail"),jsonObject.getString("FriendList"),jsonObject.getString("Type"))
                    noteList.add(n)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
            adapter.notifyDataSetChanged()
            swipeRefresh.isRefreshing=false
        }
    }

    //菜单栏的处理
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    //点击标题右上角的菜单栏时打开侧滑菜单
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
            //R.id.action_settings -> true
            R.id.action_type1->{
                type="note_of_user"
                initNotes1()
            }
            R.id.action_type2->{
                type="note_of_friend"
                initNotes2()
            }
            R.id.action_type5->{
                type="note_of_user_finish"
                initNotes4()
            }
            R.id.action_type6->{
                type="note_of_friend_finish"
                initNotes5()
            }
            R.id.action_type3->{
                type="会议"
                initNotes3()
            }
            R.id.action_type4->{
                type="出行"
                initNotes3()
            }
        }
        return true
    }
}
//模拟器换成mumu不然不能上网，再下面的terminal，里输入adb connect 127.0.0.1：7555即可