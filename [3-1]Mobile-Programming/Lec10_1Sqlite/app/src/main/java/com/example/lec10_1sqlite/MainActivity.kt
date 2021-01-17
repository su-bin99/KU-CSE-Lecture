package com.example.lec10_1sqlite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var myDBHelper : MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        getAllRecord()
    }

    fun init(){
        myDBHelper = MyDBHelper(this)
        insertBtn.setOnClickListener {
            val quantity = pQuantityEdit.text.toString().toInt()
            val name = pNameEdit.text.toString()
            val product = Product(0, name, quantity)
            val result = myDBHelper.insertProduct(product)
            if( result){
                Toast.makeText(this, "DB INSERT SUCCESS ", Toast.LENGTH_SHORT).show()
                getAllRecord()
            } else{
                Toast.makeText(this, "DB INSERT FAIL ", Toast.LENGTH_SHORT).show()
            }
        }

        deleteBtn.setOnClickListener {
            val id = pIdEdit.text.toString()
            val result = myDBHelper.deleteProduct(id)
            if(result){
                Toast.makeText(this, "DELETE SUCCESS", Toast.LENGTH_SHORT).show()
                getAllRecord()
            } else{
                Toast.makeText(this, "DELETE FAIL", Toast.LENGTH_SHORT).show()
            }
        }

        updateBtn.setOnClickListener {
            val id = pIdEdit.text.toString().toInt()
            val name = pNameEdit.text.toString()
            val quantity = pQuantityEdit.text.toString().toInt()

            val product = Product(id, name, quantity)
            val result = myDBHelper.updateProduct(product)
            if( result){
                Toast.makeText(this, "UPDATE SUCCESS ", Toast.LENGTH_SHORT).show()
                getAllRecord()
            } else{
                Toast.makeText(this, "UPDATE FAIL ", Toast.LENGTH_SHORT).show()
            }
        }

        findBtn.setOnClickListener {
            val name = pNameEdit.text.toString()
            val result = myDBHelper.findProduct(name)
            if( result){
                Toast.makeText(this, "RECORD FOUND", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "NO MATCH FOUND", Toast.LENGTH_SHORT).show()
            }
        }

        testSql.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val name = s.toString()
                val result = myDBHelper.findProduct2(name)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    fun getAllRecord(){
        myDBHelper.getAllRecord()
    }

}
