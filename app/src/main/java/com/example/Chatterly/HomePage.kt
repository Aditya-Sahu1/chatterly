package com.example.Chatterly


import AllUsersResponse
import User
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomePage : AppCompatActivity() {
    private lateinit var userRecyclerView:RecyclerView
    private lateinit var userList:ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var  sharedPref:SharedPreferences

    override  fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.title ="Chats"
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPref = getSharedPreferences("chat_app", MODE_PRIVATE)


        userList= ArrayList()
        adapter= UserAdapter(this,userList)
        userRecyclerView=findViewById(R.id.user_recycler)
        userRecyclerView.layoutManager=LinearLayoutManager(this)
        userRecyclerView.adapter=adapter
        val token=sharedPref.getString("token",null)

        val client=OkHttpClient.Builder()
            .addInterceptor(authintercepter(token))
            .build()
        val retrofitBuilder=Retrofit.Builder()
            .baseUrl("https://chatterly-backend-raog.onrender.com/chatapp/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(RetrofitAPIInsterface::class.java)
        val rdata=retrofitBuilder.getAllUsers()
        Sockethandler.setSocket()
        Sockethandler.connect()
        val socket=Sockethandler.getSocket()
        val userId=sharedPref.getString("user_id",null)
        socket.emit("join",userId)
        rdata.enqueue(object : Callback<AllUsersResponse?> {
            override fun onResponse(call: Call<AllUsersResponse?>, response: Response<AllUsersResponse?>) {
                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!.users
                    if (users != null && users.isNotEmpty()) {
                        userList.clear()
                        userList.addAll(users)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@HomePage, "No users found in response", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@HomePage, "Response failed: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<AllUsersResponse?>, t: Throwable) {
                Toast.makeText(this@HomePage,"${t}",Toast.LENGTH_LONG).show()
            }
        })
    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.loogOut){
            sharedPref.edit().clear().apply()
            Sockethandler.getSocket().disconnect()
            startActivity(Intent(this@HomePage, LoginActivity::class.java))
            finish()
            return true;
        }
        return true;
    }
}