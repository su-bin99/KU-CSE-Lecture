package com.example.lec4_3dollcostume

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val checkBox = mutableListOf<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init(){
        checkBox.add(checkBox1)
        checkBox.add(checkBox2)
        checkBox.add(checkBox3)
        checkBox.add(checkBox4)
        checkBox.add(checkBox5)
        checkBox.add(checkBox6)
        checkBox.add(checkBox7)
        checkBox.add(checkBox8)
        checkBox.add(checkBox9)
        checkBox.add(checkBox10)

        for(check in checkBox){
            //checkBox마다 이벤트를 달아줌
            check.setOnCheckedChangeListener { buttonView, isChecked ->
                val imgID = resources.getIdentifier(buttonView.text.toString(), "id", packageName)
                //checkBox에 해당하는 text로 id를 찾음
                val imgView = findViewById<ImageView>(imgID)
                //그 id로 이미지를 찾아옴
                if(isChecked){
                    imgView.visibility = View.VISIBLE
                }else{
                    imgView.visibility = View.INVISIBLE
                }
            }
        }
    }

}
