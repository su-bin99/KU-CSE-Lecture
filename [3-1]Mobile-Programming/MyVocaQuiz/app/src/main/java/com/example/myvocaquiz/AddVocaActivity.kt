package com.example.myvocaquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_voca.*
import java.io.PrintStream

class AddVocaActivity : AppCompatActivity() {
    var dataArray : ArrayList<MyData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_voca)
        init()
    }

    private fun init(){
        addBtn.setOnClickListener {
            val word = vocEdit.text.toString()
            val mean = meanEdit.text.toString()
            if(word == "" && mean == ""){
                Toast.makeText(this,"단어와 뜻이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }else if( word == "") {
                Toast.makeText(this, "단어가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }else if(mean == ""){
                Toast.makeText(this, "뜻이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }else{
                writeFile(word, mean)
                vocEdit.setText(null)
                meanEdit.setText(null)
            }
        }

        gotoQuizbtn.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            i.putExtra("voc", dataArray)
            if(!dataArray.isEmpty()) setResult(Activity.RESULT_OK, i)
            finish()

        }
    }

    private fun writeFile(word: String, mean: String) {
        val output = PrintStream(openFileOutput("firstLoad.txt", Context.MODE_APPEND))
        output.println(word)
        output.println(mean)
        dataArray.add(MyData(word, mean))
        output.close()
        Toast.makeText(this, word+"가 입력되었습니다.", Toast.LENGTH_SHORT).show()
    }
}
