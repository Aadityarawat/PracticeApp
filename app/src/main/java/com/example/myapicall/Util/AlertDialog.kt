package com.example.myapicall.Util

import android.app.AlertDialog
import android.content.Context

class AlertDialog {
    fun logoutDialog(context : Context){
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want to proceed?")

        builder.setPositiveButton("Yes"){
                _, _ ->
            //navigateToLogin()
        }
        builder.setNegativeButton("No"){
                _,_ ->
        }

        val alert : AlertDialog = builder.create()
        alert.setCancelable(false)
        alert.show()
    }
}