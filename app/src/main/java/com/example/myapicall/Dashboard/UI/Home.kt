package com.example.myapicall.Dashboard.UI

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewbinding.ViewBinding
import com.example.myapicall.MainActivity
import com.example.myapicall.R
import com.example.myapicall.UserAuth.Model.Constant
import com.example.myapicall.UserAuth.Model.RegisterResponse
import com.example.myapicall.UserAuth.Network.RestApi
import com.example.myapicall.UserAuth.Network.Service
import com.example.myapicall.databinding.ActivityHomeBinding
import com.example.myapicall.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding : ActivityHomeBinding
    private lateinit var drawerLayout : DrawerLayout
    private var item1: MenuItem? = null
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        toolbar.setTitle("Welcome!")

        setSupportActionBar(toolbar)

        drawerLayout = binding.drawerLayout

        val navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.framelayout2,HomeFragment.newInstance()).commit()
            binding.toolbarIV.setBackgroundResource(R.drawable.logout)
            navigationView.setCheckedItem(R.id.nav_home)
        }
        profileName()
        toolbarImageView()

    }

    private fun toolbarImageView() {
        var log = false
        binding.toolbarIV.setOnClickListener {
            if (log || (item1 == null) || (item1?.itemId == R.id.nav_home)){
                logoutDialog(this)
            }
            else if ((item1?.itemId == R.id.nav_addRequest)){
                supportFragmentManager.beginTransaction().replace(R.id.framelayout2, HomeFragment())
                    .commit()
                binding.toolbarTV.text = "Your Recent Requests"
                binding.toolbarIV.setBackgroundResource(R.drawable.logout)
                log = true
            }

        }

    }

    @SuppressLint("ResourceType")
    private fun profileName() {
        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val data = sharedPref.getString("name",null)
        Log.d("data","$data")

        val headerView = binding.navView.getHeaderView(0)

        val nav_name = headerView.findViewById<TextView>(R.id.nav_name)
        if (nav_name != null && !data.isNullOrEmpty()){
            nav_name.text = data
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item1 = item
        when(item.itemId){
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.framelayout2, HomeFragment()).commit()
                binding.toolbarTV.text = "Your Recent Requests"
                binding.toolbarIV.setBackgroundResource(R.drawable.logout)
                try {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    //setSupportActionBar(binding.toolbar)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
            R.id.nav_addRequest -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.framelayout2, RequestFragment()).addToBackStack("home").commit()

                binding.toolbarTV.text = "Submit your Request"
                binding.toolbarIV.setBackgroundResource(R.drawable.close)
                try {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }catch (e : Exception){
                    e.printStackTrace()
                }

            }
            R.id.nav_privacy -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.framelayout2, PolicyFragment()).commit()
                binding.toolbarTV.text = " "
                binding.toolbarIV.setBackgroundResource(R.color.white)
                try {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
            R.id.nav_log -> {
                logoutDialog(this)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        }else{
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        }

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
    private fun logoutDialog(context : Context){
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want to proceed?")

        builder.setPositiveButton("Yes"){
                _, _ ->
            deleteFCMToken()
            navigateToLogin()
        }
        builder.setNegativeButton("No"){
                _,_ ->
        }

        val alert : AlertDialog = builder.create()
        alert.setCancelable(false)
        alert.show()
    }

    private fun deleteFCMToken() {
        val sharedpref = this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val data = sharedpref.getString("token",null)

        val map : HashMap<String,String?> = HashMap()
        map[Constant.fcm_Token] = ""
        val service = RestApi.getRetrofitInstance().create(Service::class.java).postFCMToken("application/json",
            "Bearer $data",map)
        service.enqueue(object : Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful){
                    Log.d("deleted FCMToken","${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.d("deleted FCMToken","$t")
            }

        })
    }

    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        Constant.token = ""
        saveSharedPref()
        startActivity(intent)
        finish()
    }
    private fun saveSharedPref(){
        val sharePref = this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putString("token", Constant.token)
        editor.apply()
    }

}
