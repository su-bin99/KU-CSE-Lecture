package com.example.myvocaquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    val random = Random()
    var words = mutableMapOf<String, String>()
    var array = ArrayList<String>()
    var score : Int = 0
    var wordNum : Int = 0
    var answerIndex : Int = 0
    val ADDVOC_REQUEST = 100

    lateinit var adapter : MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        readFile()
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    fun init(){
        wordNum = array.size
        wordnumText.text = wordNum.toString()
        scoreText.text = score.toString()
        newWord() //여기서 정답의 인덱스 초기화

        recyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        adapter = MyAdapter(array, words, answerIndex)

        adapter.itemClickListener = object:MyAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: MyAdapter.MyViewHolder,
                view: View,
                data: String,
                position: Int
            ) {
                when(position){
                    adapter.answerViewIndex ->{
                        score += 10
                        Toast.makeText(applicationContext, "정답입니다.", Toast.LENGTH_SHORT).show()
                        init()
                    }
                    else ->{
                        score -= 5
                        scoreText.text = score.toString()
                        Toast.makeText(applicationContext, "정답이 아닙니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        recyclerView.adapter = adapter

        restartbtn.setOnClickListener {
            score = 0
            init()
        }

        gotoAddbtn.setOnClickListener {
            val i = Intent(this, AddVocaActivity::class.java)
            startActivityForResult(i,ADDVOC_REQUEST )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            ADDVOC_REQUEST ->{
                if(resultCode == RESULT_OK){
                    val num = data?.getIntExtra("int",-1)
                    val dataArray = data?.getSerializableExtra("voc") as ArrayList<MyData>
                    var str = ""
                    for(i in dataArray){
                        if(dataArray.size == 1) str = i.word
                        else str += i.word + " , "
                        array.add(i.word)
                        words[i.word] = i.meaning
                    }
                    Toast.makeText(this,  "추가된 단어\n : "+ str, Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,  "추가된 단어가 없습니다.", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    fun newWord(){
        answerIndex = random.nextInt(array.size)
        wordText.text = array[answerIndex]
    }

    fun readFileScan(scan : Scanner){
        while(scan.hasNextLine()){
            val word = scan.nextLine()
            val meaning = scan.nextLine()
            words[word] = meaning
            array.add(word)
        }
        scan.close()
    }

    fun readFile(){

        try{
            val scan2 = Scanner(openFileInput("firstLoad.txt"))
            readFileScan(scan2)
        }catch(e: FileNotFoundException){
            null
        }
        val scan = Scanner(resources.openRawResource(R.raw.words))
        readFileScan(scan)

    }

}
