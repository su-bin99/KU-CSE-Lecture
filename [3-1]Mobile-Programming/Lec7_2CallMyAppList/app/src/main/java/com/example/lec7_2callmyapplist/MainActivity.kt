
package com.example.lec7_2callmyapplist

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val APPLIST_REQUEST = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener { btnAction() }
    }
    fun btnAction(){
        val intent = Intent("com.example.lec7_1myapplist")
        if(ActivityCompat.checkSelfPermission(this,
                "com.example.applistpermission")
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf("com.example.applistpermission"), APPLIST_REQUEST)
        }else{
            startActivity(intent)
            //여기서 바로 실행하면 좋은데 dagerous등급이라서 승인과정 필요
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == APPLIST_REQUEST){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                btnAction()
            }else
                finish()
        }
    }

}

