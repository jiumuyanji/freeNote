package com.example.freenote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_note.*


class addNote : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        setSupportActionBar(toolbarOfAdd)
        val missingOfNoteType=intent.getStringExtra("noteType")
        val spinnerOfNoteType=findViewById<Spinner>(R.id.noteType)
        val array:Array<String> = arrayOf("多人会议","与人见面")
        val adapter:ArrayAdapter<String> = ArrayAdapter(this,R.layout.spinner_item,array)
        spinnerOfNoteType.adapter=adapter
        spinnerOfNoteType.setSelection(missingOfNoteType.toString().toInt())
        spinnerOfNoteType.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                when(position){

                    0-> {
                        Toast.makeText(this@addNote, "0000", Toast.LENGTH_SHORT).show()
                    }
                    1->Toast.makeText(this@addNote,"1111",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
               TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.done->Toast.makeText(this,"111",Toast.LENGTH_SHORT).show()
        }
        return true
    }
}
