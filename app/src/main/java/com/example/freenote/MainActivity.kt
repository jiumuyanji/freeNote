package com.example.freenote


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList
import com.google.android.material.navigation.NavigationView




class MainActivity : AppCompatActivity() {

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
            }
            return true
        }
    })
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
