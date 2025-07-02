package com.example.chatapp


import AllUsersResponse
import User
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.ArrayList


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomePage : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userRecyclerView:RecyclerView
    private lateinit var userList:ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mDBref: DatabaseReference

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
        val sharedPref = getSharedPreferences("chat_app", MODE_PRIVATE)


        mAuth=FirebaseAuth.getInstance()
        mDBref=FirebaseDatabase.getInstance().getReference()
        userList= ArrayList()
        adapter= UserAdapter(this,userList)
        userRecyclerView=findViewById(R.id.user_recycler)
        userRecyclerView.layoutManager=LinearLayoutManager(this)
        userRecyclerView.adapter=adapter
        val token=getSharedPreferences("chat_app", MODE_PRIVATE).getString("token",null)
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
        val sharedPref = getSharedPreferences("chat_app", MODE_PRIVATE)

        if(item.itemId==R.id.loogOut){
//            mAuth.signOut()
            sharedPref.edit().remove("token").apply()
                // or clear everything:
            sharedPref.edit().clear().apply()

            intent=Intent(this@HomePage, LoginActivity::class.java)
            finish()
            startActivity(intent)
            return true;
        }
        return true;
    }
}