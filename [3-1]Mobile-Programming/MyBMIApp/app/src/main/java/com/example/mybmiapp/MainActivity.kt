package com.example.mybmiapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    //버튼에 해당하는 이벤트
    //OnCreate함수 : activity가 생성되면 호출되는 함수
    //이 함수 내에서 (super)부모에 해당하는 create도 호출,
    //setcontextView함수는 디자인한 activity파일을 불러서 메모리에 객체들을 만들어두는

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

    }

    fun init(){

        button.setOnClickListener {
            //onclick은 인자가 view하나밖에 없으니까 생략해도됨
            val bmi = 몸무게.text.toString().toInt() / (키.text.toString().toInt()/100.0).pow(2.0)
                    //몸무게라는 id를 갖고있는 EditText에 입력되는 문자를 text로 가져옴
            var result:String = ""
            when{
                bmi >= 35 ->{
                    result = "고도비만"
                    imageView.setImageResource(R.drawable.ic_sentiment_dissatisfied_black_24dp)
                }
                bmi >= 23 ->{
                    result = "과체중"
                    imageView.setImageResource(R.drawable.ic_sentiment_neutral_black_24dp)
                }
                bmi >= 18.5 ->{
                    result = "정상"
                    imageView.setImageResource(R.drawable.ic_sentiment_satisfied_black_24dp)
                }
                else ->{
                    result = "저체중"
                    imageView.setImageResource(R.drawable.ic_sentiment_neutral_black_24dp)

                }
            }
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            //show()를 해야 보여지는것

        }

    }
}
