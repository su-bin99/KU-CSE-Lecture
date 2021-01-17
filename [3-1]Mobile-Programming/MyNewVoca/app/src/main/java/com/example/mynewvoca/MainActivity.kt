package com.example.mynewvoca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val textArray = arrayListOf<String>("Voca", "Game", "Add")

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
    }

}
