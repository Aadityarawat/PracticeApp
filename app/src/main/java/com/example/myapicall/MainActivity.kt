package com.example.myapicall

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import com.example.myapicall.Dashboard.UI.Home
import com.example.myapicall.UserAuth.Model.Constant
import com.example.myapicall.UserAuth.UI.LoginFragment
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedpref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val getData = sharedpref.getString("token",null)

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("token in main", Constant.token)
            if (getData != ""){
                Constant.token = getData.toString()
                Log.d("token in main if", Constant.token)
                val intent = Intent(this@MainActivity,Home::class.java)
                startActivity(intent)
                finish()
            }else{
                Log.d("token in main else", Constant.token)
                navigateToLogin()
            }
        },3000)
    }

    private fun navigateToLogin() {
        supportFragmentManager.beginTransaction().replace(R.id.framelayout,LoginFragment.newInstance()).commit()
    }
}