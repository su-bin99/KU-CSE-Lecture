package com.example.lec5_2myengvoc

import java.io.Serializable

data class MyData(var word:String, var meaning:String,
                  var check:Boolean = false) :Serializable{

}