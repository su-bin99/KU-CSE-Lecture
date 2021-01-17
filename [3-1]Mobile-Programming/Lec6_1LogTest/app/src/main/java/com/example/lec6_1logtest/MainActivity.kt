package com.example.lec6_1logtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    companion object{
        val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "OnCreate")
        setContentView(R.layout.activity_main) //화면구성해주는 함수
        //이걸 꼭 OnCreate에서만 해줘야되는건 아님!! Resume되기 전까지만 해주면됨!
        //onResume에다가 넣어줘도되는데 매번 화면을 다시만들어야됨
    }

    override fun onStart(){
        super.onStart()
        Log.i(TAG, "OnStart")
    }

    override fun onResume(){
        super.onResume()
        Log.i(TAG, "OnResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "OnPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "OnStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "OnRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "OnDestroy")
    }

}
