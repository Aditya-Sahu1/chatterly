package com.example.chatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.compose.runtime.currentCompositionErrors
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

import java.util.ArrayList

class MessageAdapter(val context:Context,val messageList:ArrayList<MessageResponse>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val current_id=context.getSharedPreferences("chat_app", MODE_PRIVATE).getString("user_id",null)

    val item_receive=1
    val item_sent=2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType==1){
            val view:View=LayoutInflater.from(context).inflate(R.layout.received_message,parent,false)
            return  receivedViewHolder(view)
        }
        else{
            val view:View=LayoutInflater.from(context).inflate(R.layout.sent_message,parent,false)
            return  sentViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        var currentMessage=messageList[position]
        if(current_id.equals(currentMessage.senderId)){
            return item_sent
        }
        else {
            return item_receive
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val  curr_message=messageList[position]
    if(holder.javaClass==sentViewHolder::class.java){
        val veiwHolder =holder as sentViewHolder
        holder.sentmsg.text=curr_message.message
    }
        else{
        val veiwHolder =holder as receivedViewHolder
        holder.receivedsmsg.text=curr_message.message
        }

    }
    class sentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentmsg = itemView.findViewById<TextView>(R.id.txt_sent_message)
    }
    class receivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receivedsmsg= itemView.findViewById<TextView>(R.id.txt_received_message)

    }
}