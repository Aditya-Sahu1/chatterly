package com.example.Chatterly

import AllUsersResponse
import okhttp3.Interceptor
import retrofit2.Call
import retrofit2.http.Body

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

class authintercepter(private val token:String?):Interceptor{
        override fun intercept(chain: Interceptor.Chain):okhttp3.Response {
                val requestBuilder= chain?.request()?.newBuilder()

                token?.let {
                        requestBuilder!!.addHeader("Authorization","Bearer $it")
                }

                return chain.proceed(requestBuilder!!.build())
        }

}

interface RetrofitAPIInsterface {


        @GET("allusers") // full URL will be http://your-api/api/users
        fun getAllUsers(): Call<AllUsersResponse>


        @POST("auth/login")
        fun loginUser(@Body req:LoginRequestModel):Call<LoginRegisterResponseModel>

        @POST("auth/register")
        fun registerUser(@Body req:RegisterRequestModel):Call<LoginRegisterResponseModel>


        @GET("getMessages") // full URL will be http://your-api/api/users
        fun getAllMessages(@Query("receiverId") recevierId:String): Call<getMessagesResponseModal>

        @POST("sendMessage")
        fun sendMessage(@Body m :sendMessageRequest):Call<sendMessageResponse>
}