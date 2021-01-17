package com.example.lec5_2myengvoc

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_voc.*
import java.io.PrintStream

class AddVocActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_voc)
        init()
    }

    private fun init(){
        addBtn.setOnClickListener {
            val word = vocEdit.text.toString()
            val mean = meanEdit.text.toString()
            writeFile(word, mean)
        }

        cancelBtn.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun writeFile(word: String, mean: String) {
        val output = PrintStream(openFileOutput("newVoc.txt", Context.MODE_APPEND))
        output.println(word)
        output.println(mean)
        output.close()

        val i = Intent()
        i.putEx
        i.putExtra("voc", MyData(word, mean))
        setResult(Activity.RESULT_OK, i)
        finish()
    }
}
