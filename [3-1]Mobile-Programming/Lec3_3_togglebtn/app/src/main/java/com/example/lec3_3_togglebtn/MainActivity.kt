package com.example.lec3_3_togglebtn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }
    fun init(){
        toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
                Toast.makeText(this, "Toggle On", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "Toggle Off", Toast.LENGTH_SHORT).show()
        }

    }

}
