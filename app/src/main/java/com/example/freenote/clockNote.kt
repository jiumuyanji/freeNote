package com.example.freenote

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_clock_note.*
import java.util.*


class clockNote : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_note)
        setSupportActionBar(toolbarOfClock)
        //一个开关当设置了闹钟后才可以取消，不然不能取消
        var clockOnOrOff=false

        //选择记录的种类，下面的toast后期改为对应的页面跳转
        val missingOfNoteType=intent.getStringExtra("noteType")
        val spinnerOfNoteType=findViewById<Spinner>(R.id.noteType)
        val array:Array<String> = arrayOf("多人会议","与人见面")
        val adapter: ArrayAdapter<String> = ArrayAdapter(this,R.layout.spinner_item,array)
        spinnerOfNoteType.adapter=adapter
        spinnerOfNoteType.setSelection(missingOfNoteType.toString().toInt())
        spinnerOfNoteType.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                when(position){
                    0-> {
                        Toast.makeText(this@clockNote, "0000", Toast.LENGTH_SHORT).show()
                    }
                    1-> Toast.makeText(this@clockNote,"1111", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        //闹钟的设置
        val intent=Intent(this@clockNote,AlarmReceiver::class.java)
        //下面的msg内容要改成记录的内容
        intent.putExtra("msg","来自设置闹钟备忘录的信息")
        //requestCode要改成该记录的id，不然闹钟取消不掉
        val pendingIntent=PendingIntent.getBroadcast(this@clockNote,0,intent,0)
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE)as AlarmManager
        clockSwitch.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked){
                    val currentTime = Calendar.getInstance()
                    val c = Calendar.getInstance()
                    c.setTimeInMillis(System.currentTimeMillis())
                    TimePickerDialog(this@clockNote, 0, TimePickerDialog.OnTimeSetListener {
                            _, hourOfDay, minute ->
                            //设置当前时间
                            // 根据用户选择的时间来设置Calendar对象
                            c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            c.set(Calendar.MINUTE, minute)
                            c.set(Calendar.SECOND,0)
                            c.set(Calendar.MILLISECOND,0)
                            // ②设置AlarmManager在Calendar对应的时间启动Activity
                            // 提示闹钟设置完毕:
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent)
                        clockOnOrOff=true
                        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), false).show()

                    DatePickerDialog(this@clockNote,0,DatePickerDialog.OnDateSetListener{
                        _,year,month,dayOfMonth->
                        c.set(Calendar.YEAR,year)
                        c.set(Calendar.MONTH,month)
                        c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                    },currentTime.get(Calendar.YEAR),currentTime.get(Calendar.MONTH),currentTime.get(Calendar.DAY_OF_MONTH)).show()
                }
                else{
                    if(clockOnOrOff){
                        alarmManager.cancel(pendingIntent)
                        clockOnOrOff=false
                        Toast.makeText(this@clockNote , "闹钟已取消", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.done-> Toast.makeText(this,"111", Toast.LENGTH_SHORT).show()
        }
        return true
    }

}



