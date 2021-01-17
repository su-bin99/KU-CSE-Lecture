package com.example.lec5_2myengvoc

import android.graphics.drawable.Drawable
import java.io.Serializable

//앱에 관련해서 가져와야하는 정보들
data class MyData(var myapplabel :String, var myappclass:String,
                  var myapppack:String, var myappicon : Drawable) :Serializable{

}