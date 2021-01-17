package com.example.lec11firebasedb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter : MyProductAdapter
    lateinit var rdb :DatabaseReference
//    var findQuary = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initBtn()
    }

    fun initBtn(){
        var a  = ArrayList<String>()
        a.add("수빈")
        a.add("현아")

        insertBtn.setOnClickListener {
            val item = Product(
                //pIdEdit.text.toString().toInt()  //이렇게 하면 안되나?
                Integer.parseInt(pIdEdit.text.toString()),
                a,
                Integer.parseInt(pQuantityEdit.text.toString())
            )
            initAdapter()
            rdb.child(pIdEdit.text.toString()).setValue(item)
            pNameEdit.setText(null)
            pIdEdit.setText(null)
            pQuantityEdit.setText(null)
        }

        deleteBtn.setOnClickListener {
            initAdapter()
            rdb.child(pIdEdit.text.toString()).removeValue()
        }

        updateBtn.setOnClickListener {
            initAdapter()
            rdb.child(pIdEdit.text.toString())
                .child("pquantity")
                .setValue(pQuantityEdit.text.toString().toInt())
        }

        findBtn.setOnClickListener {
            if(findQuary){
                findQueryAdapter()
            }else{
                findQuary= true
                findQueryAdapter()
            }
        }
    }

    fun initAdapter(){
        if(findQuary){
            findQuary = false
            if(adapter != null)
                adapter.stopListening()
            val query = FirebaseDatabase.getInstance().reference
                .child("Products/items") .limitToLast(50)
            //이 경로 에 대한 질의에 대항하는 데이터를 가져와라

            val option = FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(query, Product::class.java)
                .build()
            adapter = MyProductAdapter(option)
            adapter.itemClickListener= object:MyProductAdapter.OnItemClickListener{
                override fun OnItemClick(view: View, position: Int) {
                    pIdEdit.setText(adapter.getItem(position).pId.toString())
                    pNameEdit.setText(adapter.getItem(position).pName.size.toString())
                    pQuantityEdit.setText(adapter.getItem(position).pQuantity.toString())
                }
            }
            recyclerView.adapter = adapter
            adapter.startListening()
        }
    }

    fun findQueryAdapter(){
        if(adapter!=null)
            adapter.stopListening()
        val query = rdb.orderByChild("pname").equalTo(pNameEdit.text.toString())
        val option = FirebaseRecyclerOptions.Builder<Product>()
            .setQuery(query, Product::class.java)
            .build()
        adapter = MyProductAdapter(option)
        adapter.itemClickListener= object:MyProductAdapter.OnItemClickListener{
            override fun OnItemClick(view: View, position: Int) {
                pIdEdit.setText(adapter.getItem(position).pId.toString())
                pNameEdit.setText(adapter.getItem(position).pName.size)
                pQuantityEdit.setText(adapter.getItem(position).pQuantity.toString())
            }
        }
        recyclerView.adapter = adapter
        adapter.startListening()
    }

    fun init(){
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        rdb = FirebaseDatabase.getInstance().getReference("Products/items")
        //adapter의 생성자에 FirebaseRecyclerOptions가 필요한대
        //걔를 만들어주기 위해서는 query가 있어야됨

        val query = FirebaseDatabase.getInstance().reference
            .child("Products/items") .limitToLast(50)
        val option = FirebaseRecyclerOptions.Builder<Product>()
            .setQuery(query, Product::class.java)
            .build()
        adapter = MyProductAdapter(option)

        adapter.itemClickListener= object:MyProductAdapter.OnItemClickListener{
            override fun OnItemClick(view: View, position: Int) {
                pIdEdit.setText(adapter.getItem(position).pId.toString())
                pNameEdit.setText(adapter.getItem(position).pName.size)
                pQuantityEdit.setText(adapter.getItem(position).pQuantity.toString())
            }
        }
        recyclerView.adapter = adapter
        adapter.startListening()

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
        //실시간 데이터 베이스라 recyclerView에 notify해주지 않아도됨
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        //listening멈추도록
    }

}
