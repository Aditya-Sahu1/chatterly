package com.example.chatapp

data class getMessagesResponseModal(
    val messages: ArrayList<MessageResponse>,
    val msg:String,
    val success: Boolean
)