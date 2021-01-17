package com.example.lec11firebasedb

data class Product(
    var pId : Int ,
    var pName : ArrayList<String> ,
    var pQuantity : Int) {
//    constructor(): this(0, ArrayList, 0)
}