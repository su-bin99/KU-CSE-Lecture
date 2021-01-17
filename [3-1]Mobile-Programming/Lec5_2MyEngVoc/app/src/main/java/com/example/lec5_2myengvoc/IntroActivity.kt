package com.example.lec5_2myengvoc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    val ADDVOC_REQUEST = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        init()
    }

    private fun init(){
        gotoVoca.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
                //명시적 intent
            startActivity(i)
        }
        gotoAddVoca.setOnClickListener {
            val i = Intent(this, AddVocActivity::class.java)
            startActivityForResult(i,ADDVOC_REQUEST )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            ADDVOC_REQUEST ->{
                if(resultCode == RESULT_OK){
                    //RESULT_OK : 뭔가 전달되었다!
                    val str = data?.getSerializableExtra("voc") as MyData
                    Toast.makeText(this, str.word+"단어 추가됨", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
