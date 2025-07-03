package com.example.Chatterly

import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {


    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            val intent = Intent( this,SignUpActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            val email=edtEmail.text.toString()
            val password=edtPassword.text.toString()
            login(email,password)
        }
    }


    private fun login(email: String, password: String) {
        val retrofitBuilder= Retrofit.Builder()
            .baseUrl("https://chatterly-backend-raog.onrender.com/chatapp/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitAPIInsterface::class.java)




        val rdata=retrofitBuilder.loginUser(LoginRequestModel(email,password))
        rdata.enqueue(object : Callback<LoginRegisterResponseModel?> {
            override fun onResponse(
                call: Call<LoginRegisterResponseModel?>,
                response: Response<LoginRegisterResponseModel?>
            ) {
//                Toast.makeText( this@LoginActivity, response.body()!!.token.toString(),Toast.LENGTH_SHORT).show()
                response.body()!!.token?.let { Log.d("login response", it) }
                var resBody=response.body()
                Log.d("DEBUG", "Response body: ${response.body()}")
                Log.d("DEBUG", "Raw: ${response.raw()}")

                if(resBody!=null && resBody.success){
                    val sharedPref = getSharedPreferences("chat_app", MODE_PRIVATE)
                    sharedPref.edit().putString("token", resBody.token).apply()
                    sharedPref.edit().putString("user_id", resBody._id).apply()
                    startActivity(Intent(this@LoginActivity, HomePage::class.java))
                    finish()
                } else{
                    Toast.makeText(this@LoginActivity, response.body()?.msg.toString(),Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<LoginRegisterResponseModel?>, t: Throwable) {
//                Toast.makeText(this@LoginActivity,t.toString(),Toast.LENGTH_LONG).show()
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("LoginActivity", "onFailure: ${t.message}", t)

            }
        })

    }

}