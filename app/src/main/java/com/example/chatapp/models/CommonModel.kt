package com.example.chatapp.models

data class CommonModel(
    val id:String = "",
    var username:String = "",
    var fullname:String = "",
    var phone:String = "",
    var region:String = "",
    var status:String = "",
    var bio:String = "",
    var photoUrl:String = "empty",

    var text: String = "",
    var type: String = "",
    var from: String = "",
    var timeStamp: Any = "",
    var imageUrl: String = "empty"


) {
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }
}