package com.example.lec3_4autotextview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val countries = mutableListOf(
        //변경가능한 리스트
        "Afghanistan", "Albania", "Algeria", "American Samoa",
            "Bahrain", "Bargladesh", "Belarus", "Belgium"
    )
    lateinit var countries2:Array<String> //리소스파일에 정의해둔 배열 가져와서 저장할 공간

    lateinit var adapter:ArrayAdapter<String>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init(){
        countries2 = resources.getStringArray(R.array.countries_array)

        adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                countries  //mutablelist로 만든 배열을 활용할때
                //countries2  //리소스파일에 정의해둔 배열을 활용할때
        )

        autoCompleteTextView.setAdapter(adapter)

        multiAutoCompleteTextView.setAdapter(adapter)
        multiAutoCompleteTextView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        editText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                // editText에 값이 변경되면 버튼을 활성화하는 작업
                //Editable객체 : 입력한 String 값을 갖고있음
                val str = s.toString()
                inputBtn.isEnabled = str.isNotEmpty() //str이 empty가 아니면 트루!

                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

        inputBtn.setOnClickListener {
            adapter.add(editText.text.toString())
            adapter.notifyDataSetChanged()
                //이 데이터를 사용하던 위젯 ( autocompletetextview가 알게되는건강 )
                //여기서 저 countries배열에는 입력한 값이 들어가는건 없는건가?
            editText.text.clear() //editText에 있는 글씨 지우기
        }

    }


}
