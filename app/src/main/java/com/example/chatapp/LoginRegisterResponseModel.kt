package com.example.chatapp

data class LoginRegisterResponseModel(
    val success: Boolean,
    val token: String?,
    val msg:String?,
    val _id:String?
)