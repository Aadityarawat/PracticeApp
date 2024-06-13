package com.example.myapicall.UserAuth.Model

import java.io.Serializable

data class LoginData(
    val email : String,
    val password : String
):Serializable
