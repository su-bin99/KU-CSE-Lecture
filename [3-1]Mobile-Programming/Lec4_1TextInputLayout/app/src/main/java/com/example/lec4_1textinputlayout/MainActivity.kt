package com.example.lec4_1textinputlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init(){
        //이메일 입력에 대해서 형식체크 -> 맞지 않으면 에러출력
        emailText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().contains("@")) {
                    //@가 있으면 정상적인것
                    textInputLayout.error = null
                    //에러는 textInputLayout에다가!
                }else{
                    textInputLayout.error = "이메일 형식이 올바르지 않습니다. "
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
               // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
              //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }
}
