package com.example.myapicall.UserAuth.Model

import java.io.Serializable

data class RegisterResponse(
    val status : String,
    val message : String,
    val token : String
): Serializable
