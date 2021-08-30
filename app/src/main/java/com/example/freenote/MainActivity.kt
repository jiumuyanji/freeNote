package com.example.freenote


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_header.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {

    private val takePhoto=1
    private val fromAlbum=2
    private val path="iconImage"
    lateinit var imageUri:Uri
    lateinit var outputImage:File
    val notes= mutableListOf(Note("开会",R.drawable.paper5),Note("开会",R.drawable.paper5),Note("开会",R.drawable.paper5),Note("开会",R.drawable.paper5),Note("开会",R.drawable.paper5),Note("开会",R.drawable.paper5),Note("开会",R.drawable.paper5),Note("开会",R.drawable.paper5),Note("开会",R.drawable.paper5),Note("开会",R.drawable.paper5))
    val noteList=ArrayList<Note>(notes)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        fab.setOnClickListener {
            AlertDialog.Builder(this).apply {
                val noteTypes: Array<CharSequence> = arrayOf("多人会议", "与人见面")
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
                            startActivity(intent)
                        }
                        1->{
                            val intent = Intent(this@MainActivity, clockNote::class.java)
                            intent.putExtra("noteType", type.toString())
                            startActivity(intent)
                        }
                    }
                }
                show()
            }
        }

        initNotes()
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        val adapter = NoteAdapter(this, noteList)
        recyclerView.adapter = adapter
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            refreshNotes(adapter)
        }
        navView.setCheckedItem(R.id.navAccount)
        navView.setNavigationItemSelectedListener(object :NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem):Boolean{
            when(item.itemId){
                R.id.navAccount->{
                    val intent = Intent(this@MainActivity, addNote::class.java)
                    intent.putExtra("noteType", "0")
                    startActivity(intent)
                }
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
            }
            return true
        }
    })
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
}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            takePhoto->{
                if(resultCode==Activity.RESULT_OK){
                    val bitmap=BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    iconImage.setImageBitmap(rotateIfRequired(bitmap))
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

    private fun getBitmapFromUri(uri: Uri)=contentResolver.openFileDescriptor(uri,"r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    private fun refreshNotes(adapter: NoteAdapter){
        initNotes()
        adapter.notifyDataSetChanged()
        swipeRefresh.isRefreshing=false
    }

    private fun initNotes(){}

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
            //R.id.action_settings -> true

            else -> super.onOptionsItemSelected(item)

        }
        return true
    }

}
