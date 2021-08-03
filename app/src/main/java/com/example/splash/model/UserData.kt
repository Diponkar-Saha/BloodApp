package com.example.splash.model

data class UserData(
    val id: String = "",
    var name: String? = null,
    var phone: String? = null,
    var email: String? = null,
    val secondPhone:String = "",
    val gender:String = "",
    val age:Int = 0,
    val place:String = "",
    val city:String = "",
    val bloodGroup:String = "",
    var date:String = "",
    var image:String = ""
)