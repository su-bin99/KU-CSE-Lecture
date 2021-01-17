package com.example.lec3_2_radiobtn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init(){
        imageGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButton1 -> imageView.setImageResource(R.drawable.img1)
                R.id.radioButton2 -> imageView.setImageResource(R.drawable.img2)
                R.id.radioButton3 -> imageView.setImageResource(R.drawable.img3)
            }
        }
    }
}

