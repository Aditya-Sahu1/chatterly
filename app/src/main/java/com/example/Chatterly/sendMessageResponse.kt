package com.example.chatapp

data class sendMessageResponse(
    var success:Boolean,
    var msg:String,
    var message: MessageResponse
)
