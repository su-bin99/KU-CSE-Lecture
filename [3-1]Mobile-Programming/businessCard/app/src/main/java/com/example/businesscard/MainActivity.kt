package com.example.businesscard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
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
                R.id.button1 -> imgView.setImageResource(R.drawable.subin)
                R.id.button2 -> imgView.setImageResource(R.drawable.subin2)
                R.id.button3 -> imgView.setImageResource(R.drawable.subin3)
            }
        }

        toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                Toast.makeText(this, "소개를 숨기고싶다면 버튼을 다시 터치해주세요.", Toast.LENGTH_SHORT).show()
                introduceText.visibility = View.VISIBLE
            }
            else {
                Toast.makeText(this, "소개를 보고싶다면 버튼을 다시 터치해주세요. ", Toast.LENGTH_SHORT).show()
                introduceText.visibility = View.INVISIBLE
            }
        }



    }


}
