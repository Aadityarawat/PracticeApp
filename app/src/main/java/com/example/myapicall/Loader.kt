package com.example.myapicall

import android.app.Activity
import android.app.AlertDialog
import androidx.fragment.app.Fragment

class Loader(val mActivity : Fragment) {
    private lateinit var isdialog : AlertDialog

    fun startloader(){
        val inflater = mActivity.layoutInflater
        val dialog = inflater.inflate(R.layout.loader,null)

        val builder = AlertDialog.Builder(mActivity.requireContext())
        builder.setView(dialog)
        builder.setCancelable(false)
        isdialog = builder.create()
        isdialog.show()
    }

    fun stopLoader(){
        isdialog.dismiss()
    }
}