package com.example.Chatterly

data class getMessagesResponseModal(
    val messages: ArrayList<MessageResponse>,
    val msg:String,
    val success: Boolean
)