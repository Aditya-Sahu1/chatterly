package com.example.Chatterly

import java.util.Date

data class MessageResponse(
    val isImage: Boolean,
    val message: String,
    val roomId: String,
    val senderId: String,
    val timestamp: Date
)