package com.example.Chatterly

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpActivity : AppCompatActivity() {
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtName: EditText
    private lateinit var btnSignUp: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edt_name)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        edtName = findViewById(R.id.edt_name)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {

            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            if (validateInputs(name, email, password)) {
                Toast.makeText(this,email,Toast.LENGTH_LONG).show()
                signup(email, password,name)
            }
        }
    }
    private fun signup(email: String, password: String, name: String) {
        // logic of creating user

        val retrofitBuilder= Retrofit.Builder()
            .baseUrl("https://chatterly-backend-raog.onrender.com/chatapp/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitAPIInsterface::class.java)




        val rdata=retrofitBuilder.registerUser(RegisterRequestModel(email,name,password))
        rdata.enqueue(object : Callback<LoginRegisterResponseModel?> {
            override fun onResponse(
                call: Call<LoginRegisterResponseModel?>,
                response: Response<LoginRegisterResponseModel?>
            ) {
                Toast.makeText( this@SignUpActivity, response.body()!!.token.toString(),Toast.LENGTH_SHORT).show()
                Log.d("RESPONSE_CODE", response.code().toString())
                Log.d("RESPONSE_BODY", response.body().toString())
                if(response.body()!!.success){
                    val sharedPref = getSharedPreferences("chat_app", MODE_PRIVATE)
                    sharedPref.edit().putString("token", response.body()!!.token).apply()
                    sharedPref.edit().putString("user_id", response.body()!!._id).apply()
                    startActivity(Intent(this@SignUpActivity, HomePage::class.java))
                    finish()
                } else{
                    Toast.makeText(this@SignUpActivity, response.body()?.msg.toString(),Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<LoginRegisterResponseModel?>, t: Throwable) {
                Toast.makeText(this@SignUpActivity,t.toString(),Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun validateInputs(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
