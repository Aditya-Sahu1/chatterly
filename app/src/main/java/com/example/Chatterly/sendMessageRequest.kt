package com.example.Chatterly

data class sendMessageRequest(
    var message:String,
    var receiverId:String,
    var isImage:Boolean
)
