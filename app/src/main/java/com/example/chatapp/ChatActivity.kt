package com.example.chatapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatActivity : AppCompatActivity() {


    private lateinit var chatRecylceriew:RecyclerView
    private lateinit var messageBox:TextView
    private lateinit var sendButton:ImageView
    private  lateinit var messageList:ArrayList<MessageResponse>
    private lateinit var  mesageAdapter: MessageAdapter
    var receiverId :String?=null
    private lateinit var retrofitBuilder:RetrofitAPIInsterface
    private lateinit var client: OkHttpClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val name=intent.getStringExtra("name")
        receiverId=intent.getStringExtra("_id")

        supportActionBar?.title=name
        chatRecylceriew=findViewById(R.id.ChatRecyclerView)
        messageBox=findViewById(R.id.messageBox)
        sendButton=findViewById(R.id.sent_button)
        messageList=ArrayList()
        mesageAdapter=MessageAdapter(this,messageList)
        chatRecylceriew.layoutManager=LinearLayoutManager(this)
        chatRecylceriew.adapter=mesageAdapter

        val token=getSharedPreferences("chat_app", MODE_PRIVATE).getString("token",null)

        client=OkHttpClient.Builder()
            .addInterceptor(authintercepter(token))
            .build()

        retrofitBuilder=Retrofit.Builder()
            .baseUrl("https://chatterly-backend-raog.onrender.com/chatapp/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(RetrofitAPIInsterface::class.java)

        updateMessages()
        sendButton.setOnClickListener{
            val msg=messageBox.text.toString().trim()
            if(msg != "") {
                val messageObject = sendMessageRequest(msg,receiverId!!,false)

                var sentmessage=retrofitBuilder.sendMessage(messageObject)
                sentmessage.enqueue(object : Callback<sendMessageResponse?> {
                    override fun onResponse(
                        call: Call<sendMessageResponse?>,
                        response: Response<sendMessageResponse?>
                    ) {
                        val resBody=response.body();
                        if(resBody!=null && resBody.success){
//                       if(response.body()!!.success){
                           Toast.makeText(this@ChatActivity,resBody.msg,Toast.LENGTH_LONG).show()
//                           updateMessages()
                           messageList.add(response.body()!!.message)
                           mesageAdapter.notifyItemInserted(messageList.size-1)
                           chatRecylceriew.scrollToPosition(messageList.size-1)
                           messageBox.text = ""

                       }
                        else{
                            Toast.makeText(this@ChatActivity,response.body()!!.msg,Toast.LENGTH_LONG).show()
                       }
                    }

                    override fun onFailure(call: Call<sendMessageResponse?>, t: Throwable) {
                        Toast.makeText(this@ChatActivity,t.message.toString(),Toast.LENGTH_LONG).show()

                    }
                })
            }
        }
    }

    private fun updateMessages() {

        val messages=retrofitBuilder.getAllMessages(receiverId!!)
        messages.enqueue(object : Callback<getMessagesResponseModal?> {
            override fun onResponse(
                call: Call<getMessagesResponseModal?>,
                response: Response<getMessagesResponseModal?>
            ) {
                val resBody=response.body();
                if(resBody!=null){
                    if(resBody.success){
                        messageList.clear()
                        for(m in resBody.messages){
                            messageList.add(m)
                        }
                        mesageAdapter.notifyDataSetChanged()
                        chatRecylceriew.scrollToPosition(messageList.size - 1)
                    }
                    else{
                        Toast.makeText(this@ChatActivity,resBody.msg.toString(),Toast.LENGTH_LONG).show()
                    }

                }
                else{
                    Toast.makeText(this@ChatActivity,"Something  went wrong",Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<getMessagesResponseModal?>, t: Throwable) {
                Toast.makeText(this@ChatActivity,t.toString(),Toast.LENGTH_LONG).show()
            }
        })
    }
}