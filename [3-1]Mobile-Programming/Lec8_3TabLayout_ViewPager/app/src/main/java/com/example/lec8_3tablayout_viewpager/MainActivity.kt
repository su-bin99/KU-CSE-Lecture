package com.example.lec8_3tablayout_viewpager;

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CoffeeFragment.OnListFragmentInteractionListener {
        //AppcompatActivity : FragmentActivity를 상속받음 ( 컨트롤H )
    val textArray = arrayListOf<String>("이미지", "리스트")
    //탭의 이름들을 arraylist로

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init(){
        viewPager.adapter = MyFragStateAdapter(this)
        TabLayoutMediator(tabLayout, viewPager){
                tab: TabLayout.Tab, position: Int ->
            tab.text = textArray[position]
        }.attach()
        //뷰페이저랑 탭이랑 연결
    }

    override fun onListFragmentInteraction(item: String?) {
        Toast.makeText(this, item, Toast.LENGTH_SHORT).show()
    }
}
