package com.example.myapicall.UserAuth.Network

import com.example.myapicall.UserAuth.Model.Constant
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


class RestApi {

    companion object{

        fun getRetrofitInstance(): Retrofit{
            val gson = GsonBuilder()
                .setLenient()
                .create()
            return Retrofit.Builder()
                .baseUrl("http://demo.kitlabs.us/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        fun getQuoteAPI(retrofit: Retrofit): Service{
            return retrofit.create(Service::class.java)
        }

    }

}