package com.example.chatapp

data class sendMessageRequest(
    var message:String,
    var receiverId:String,
    var isImage:Boolean
)
