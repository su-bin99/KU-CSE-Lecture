
package com.example.lec8_2dynamicfragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CoffeeFragment.OnListFragmentInteractionListener {

    val imageFragment = imageFragment()
    val coffeeFragment = CoffeeFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    override fun onListFragmentInteraction(item: String?) {
        Toast.makeText(this, item, Toast.LENGTH_SHORT).show()
    }

    private fun init(){
        val fragment = supportFragmentManager.beginTransaction()
        fragment.addToBackStack(null)
        fragment.replace(R.id.frame, imageFragment)
        fragment.commit()
        button.setOnClickListener {
            if(!imageFragment.isVisible){
                val fragment = supportFragmentManager.beginTransaction()
                fragment.addToBackStack(null)
                fragment.replace(R.id.frame, imageFragment)
                fragment.commit()
            }
        }
        button2.setOnClickListener {
            if(!coffeeFragment.isVisible){
                val fragment = supportFragmentManager.beginTransaction()
                fragment.addToBackStack(null)
                fragment.replace(R.id.frame, coffeeFragment)
                fragment.commit()
            }
        }
    }
}

