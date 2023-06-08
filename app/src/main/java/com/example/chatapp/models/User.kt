package com.example.chatapp.models

data class User(
    val id:String = "",
    var username:String = "",
    var fullname:String = "",
    var phone:String = "",
    var region:String = "",
    var status:String = "",
    var bio:String = "",
    var photoUrl:String = "empty"
)