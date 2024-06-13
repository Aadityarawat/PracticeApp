package com.example.myapicall.Dashboard.Model

import java.io.Serializable


data class FamilyData (
    val request_from : String,
    val contact_name : String,
    val contact_email : String,
    val contact_phone : String,
    val person_age : String,
    val support_type : String,
    val staff_gender : String,
    val staff_vehicle : String,
    val have_pets : String,
    val request_title : String,
    val about : String,
    val address_line_1 : String,
    val address_line_2 : String,
    val city : String,
    val state : String,
    val zip : String,
    val image : String,
    val staffing_need_time : String?
): Serializable