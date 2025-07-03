package com.example.Chatterly

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object Sockethandler {
    private lateinit var mSocket:Socket
    fun setSocket(){
        try {
            mSocket= IO.socket("https://chatterly-backend-raog.onrender.com")
        }
        catch(e:URISyntaxException){
            Log.e("SocketHandler","{e.message}")
        }
    }
    fun getSocket():Socket{
        return mSocket
    }
    fun connect(){
        mSocket.connect()
    }
    fun disconneect(){
        mSocket.disconnect()
    }
}