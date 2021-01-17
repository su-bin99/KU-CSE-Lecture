package com.example.lec7_1myapplist

import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lec5_2myengvoc.MyAdapter
import com.example.lec5_2myengvoc.MyData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
//핸드폰에 등록된 앱 정보를 가지고 와서 리스트를 작성하고,
// 클릭할 경우 해당 앱을 실행한다.
    lateinit var adapter : MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init(){
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = MyAdapter(ArrayList<MyData>())
        recyclerView.adapter = adapter
        val intent = Intent(ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        //인텐트에 main이라는 액션이 정의되어있고, 카테고리에 런처가 등록되어있는 액티비티를 찾아서 뿌려주기
        val applist = packageManager.queryIntentActivities(intent, 0)
        if(applist.size >0){
            for(appinfo in applist){
                val myapplabel = appinfo.loadLabel(packageManager)
                    //라벨에 앱인포로부터 정보를 가져옴 ( 이 앱에 해당하는 라벨 정보를 받아옴 )
                val myappclass = appinfo.activityInfo.name
                val myapppackname = appinfo.activityInfo.packageName
                val myappicon = appinfo.loadIcon(packageManager)
                adapter.items.add(MyData(myapplabel.toString(), myappclass, myapppackname, myappicon))
            }
        }
        adapter.itemClickListener = object : MyAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: MyAdapter.MyViewHolder,
                view: View,
                data: MyData,
                position: Int
            ) {
                val intent = packageManager.getLaunchIntentForPackage(adapter.items[position].myapppack)
                //패키지에 해당하는 정보로 인텐트가 만들어진 것
                startActivity(intent)
            }

        }


    }
}

