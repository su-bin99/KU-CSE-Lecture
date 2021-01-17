package com.example.lec5_1recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var data:ArrayList<MyData> = ArrayList<MyData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //activity가 생성이되면서 이 함수가 호출이 되고
        //이 메뉴가 없으면 없는채로 보이는거고
        //inflater로 메뉴를 부착하게 되면 메뉴가 달린 상태로 출력이 되는것
        menuInflater.inflate(R.menu.menu1, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //메뉴항목들을 선택할때마다 오머라이딩된 함수들이 호출되는것
        when(item.itemId){
            R.id.menuitem1 -> {
                //recyclerView.layoutManager임 ( 소문자로 l로 시작해야함! )
                recyclerView.layoutManager = LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false)
            }
            R.id.menuitem2 -> {
                recyclerView.layoutManager = GridLayoutManager(this,3)
            }
            R.id.menuitem3 -> {
                recyclerView.layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView(){
        //recyclerView에 LayoutManager, adapter지정해줌
        //LayoutMAnager : 가로로 배치할지, 세로로 배치할지 정할 수 있음
        //adapter : RecyclerView에 데이터를 제공하고
        recyclerView.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = MyAdapter(data)
    }

    private fun initData() {
        data.add(MyData("item1", 10))
        data.add(MyData("item2", 20))
        data.add(MyData("item3", 15))
        data.add(MyData("item4", 30))
        data.add(MyData("item5", 25))
        data.add(MyData("item6", 35))
        data.add(MyData("item7", 10))
        data.add(MyData("item8", 20))
        data.add(MyData("item9", 5))
        data.add(MyData("item10",8))

    }


}
