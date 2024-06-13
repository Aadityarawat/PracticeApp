package com.example.myapicall.UserAuth.Model

data class data(
    val id : Int,
    val request_id : String,
    val user_id : Int,
    val request_from : String,
    val request_title : String,
    val contact_name : String,
    val contact_email : String,
    val contact_phone: String,
    val person_age: Int,
    val support_type : String,
    val staffing_need_time : String,
    val staff_gender : String,
    val staff_vehicle: Int,
    val have_pets: Int,
    val about : String,
    val image : String,
    val address_line_1 : String,
    val address_line_2: String,
    val city : String,
    val state: String,
    val zip: String,
    val country: String,
    val request_status: String,
    val created_at: String,
    val updated_at: String
)
