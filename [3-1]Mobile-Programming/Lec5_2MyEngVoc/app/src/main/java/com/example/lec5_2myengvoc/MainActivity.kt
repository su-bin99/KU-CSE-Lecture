package com.example.lec5_2myengvoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var words = mutableMapOf<String, String>()
    var array = ArrayList<MyData>()
    lateinit var adapter : MyAdapter
    lateinit var tts : TextToSpeech
    var isTtsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    override fun onPause() {
        super.onPause()
        tts.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }

    private fun init(){
        tts = TextToSpeech(this, TextToSpeech.OnInitListener {
            isTtsReady = true
            tts.language = Locale.UK
        })
        readFile()
        recyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        adapter = MyAdapter(array, words)
        adapter.itemClickListener = object:MyAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: MyAdapter.MyViewHolder,
                view: View,
                data: String,
                position: Int
            ) {
                var meanView:TextView = view.findViewById(R.id.meaningTextView)
                adapter.changeVisiblity(meanView)

                if(isTtsReady) {
                    tts.speak(data, TextToSpeech.QUEUE_ADD, null, null)
                    Toast.makeText(applicationContext, words[data], Toast.LENGTH_SHORT).show()
                }
            }
        }
        recyclerView.adapter = adapter

        val simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP
            , ItemTouchHelper.RIGHT){   //익명클래스로 만들기

            //simpleCallback객체를 달아줘야함!!!! (중요)
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeItem(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun readFileScan(scan : Scanner){
        while(scan.hasNextLine()){
            val word = scan.nextLine()
            val meaning = scan.nextLine()
            words[word] = meaning
            array.add(MyData(word, meaning))
        }
        scan.close()
    }

    fun readFile(){
        try{
            val scan2 = Scanner(openFileInput("newVoc.txt"))
            readFileScan(scan2)
        }catch(e:FileNotFoundException ){
            null
        }

        val scan = Scanner(resources.openRawResource(R.raw.words))
        readFileScan(scan)
    }
}
