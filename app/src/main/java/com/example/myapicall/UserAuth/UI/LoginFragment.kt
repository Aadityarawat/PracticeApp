package com.example.myapicall.UserAuth.UI

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.myapicall.Dashboard.UI.Home
import com.example.myapicall.Loader
import com.example.myapicall.MainActivity
import com.example.myapicall.R
import com.example.myapicall.UserAuth.Model.Constant
import com.example.myapicall.UserAuth.Model.LoginData
import com.example.myapicall.UserAuth.Model.LoginRequest
import com.example.myapicall.UserAuth.Model.LoginResponse
import com.example.myapicall.UserAuth.Model.Register
import com.example.myapicall.UserAuth.Model.RegisterResponse
import com.example.myapicall.UserAuth.Network.RestApi
import com.example.myapicall.UserAuth.Network.Service
import com.example.myapicall.databinding.FragmentLogin2Binding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create


class LoginFragment : Fragment() {
    lateinit var loading : Loader
    private var _binding: FragmentLogin2Binding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogin2Binding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
//        getFCMToken()
//        if (currentUser != null) {
//            // The user is already signed in, navigate to MainActivity
//            val intent = Intent(this@LoginFragment.requireContext(), Home::class.java)
//            startActivity(intent)
//            activity?.finish()
//        }

        loading = Loader(this)

        emailFocusListner()
        passwirdFocusListner()

        binding.logsign.setOnClickListener {
            goToSignPage()
        }

        binding.loginbutton.setOnClickListener {
            checkValidation()
        }

        binding.googleIV.setOnClickListener {
            googleSignIn()
        }
        binding.forgotpassTV.setOnClickListener {

            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.framelayout, ForgotPassFragment())?.commit()
        }

        return binding.root
    }

    private fun getFCMToken(){
        Log.d("FCMToken","process")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful){
                Log.d("FCMToken","Fetching FCM registration token failed")
                return@addOnCompleteListener
            }
            val token: String = task.result
            Log.d("FCMToken", "onComplete: $token")
            saveFCMToken(token)
        }
    }

    private fun saveFCMToken(token: String){
       // Constant.fcm_Token = token

        val sharedPreferences = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("fcmToken", token)
        editor.apply()

        val map : HashMap<String, String?> = HashMap()
        map[Constant.fcm_Token] = token
        val service = RestApi.getRetrofitInstance().create(Service::class.java).postFCMToken("application/json","Bearer "+Constant.token,map)

        service.enqueue(object : Callback<RegisterResponse>{
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful){
                    Log.d("FCMToken Success","${response.body()?.message}")
                }
                else{
                    when (response.code()){
                        400 -> Toast.makeText(requireContext(),"FCMToken Bad Request",Toast.LENGTH_LONG).show()
                        401 -> Toast.makeText(requireContext(),"FCMToken Unauthorized",Toast.LENGTH_LONG).show()
                        404 -> Toast.makeText(requireContext(),"FCMToken Not Found",Toast.LENGTH_LONG).show()
                        500 -> Toast.makeText(requireContext(),"FCMToken Internal Server Error",Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.d("FCMToken failed","$t")
            }
        })

    }

    private fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("activitygoogle","$requestCode")
        if (requestCode == RC_SIGN_IN){
            Log.d("activitygoogle","2")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.d("activitygoogle","3")
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e : ApiException){
                Toast.makeText(requireContext(), "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d("firebase","1")
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        Log.d("firebase","2")
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("firebase","3")
                    val user = auth.currentUser
                    Toast.makeText(requireContext(), "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    getFCMToken()
                    startActivity(Intent(this@LoginFragment.requireContext(),Home::class.java))
                    activity?.finish()
                } else {
                    Log.d("firebase","4")
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkValidation() {
        val emailtext = binding.logemail.text.toString()
        val passtext = binding.logpass.text.toString()

        if (emailtext.isEmpty() || passtext.isEmpty()){
            if(emailtext.isEmpty()){
                binding.emailContainer.boxStrokeColor = Color.RED
                binding.emailContainer.helperText = "Can not be Empty"

            }
            if(passtext.isEmpty()){
                binding.passContainer.boxStrokeColor = Color.RED
                binding.passContainer.helperText = "Can not be Empty"
            }
        }
        else if(passValid() == null && emailValid() == null ){
            postLogin()
        }
        else{
            Toast.makeText(requireContext(),"Please Provide Valid Details", Toast.LENGTH_LONG).show()
        }
    }

    private fun passwirdFocusListner() {
        binding.logpass.setOnFocusChangeListener { _, focused ->
            if (!focused){
                binding.passContainer.helperText = passValid()
            }
        }
    }

    private fun passValid(): String? {
        val passText = binding.logpass.text.toString()
        if(passText.isEmpty()){
            binding.passContainer.boxStrokeColor = Color.RED
            return "Please Enter Your Password"
        }
        binding.passContainer.boxStrokeColor = Color.parseColor("#4899EA")
        return null
    }

    private fun emailFocusListner() {
        binding.logemail.setOnFocusChangeListener { _, focused ->
            if (!focused){
                binding.emailContainer.helperText = emailValid()
            }
        }
    }

    private fun emailValid(): String? {
        val emailET = binding.logemail.text.toString()
        if(emailET.isEmpty()){
            binding.emailContainer.boxStrokeColor = Color.RED
            return "Can not be Empty"
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailET).matches()){
            binding.emailContainer.boxStrokeColor = Color.RED
            return "Invalid Email Address"
        }
        binding.emailContainer.boxStrokeColor = Color.parseColor("#4899EA")
        return null
    }

    private fun goToSignPage() {
        val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.framelayout,SignInFragment.newInstance()).addToBackStack("login").commit()
    }

    private fun postLogin() : Boolean{
        var flag = false
        val retrofit = RestApi.getRetrofitInstance()
        val data = LoginData(
            email = binding.logemail.text.toString(),
            password = binding.logpass.text.toString()
        )
        val service = retrofit.create(Service::class.java).postLogin(data)
        loading.startloader()
        service.enqueue(object : Callback<LoginRequest>{
            override fun onResponse(
                call: Call<LoginRequest>,
                response: Response<LoginRequest>
            ) {
                if(response.isSuccessful){
                    loading.stopLoader()
                    Toast.makeText(requireContext(), "${response.body()?.status}",Toast.LENGTH_LONG).show()
                    Constant.token = response.body()?.message?.token.toString()
                    Constant.name = response.body()?.data?.name.toString()
                    saveSharedPref()
                    Log.d("token in login","${Constant.token}")

                    navigateToDashBoard()
                }else{
                    loading.stopLoader()
                    when (response.code()){
                        400 -> Toast.makeText(requireContext(),"Bad Request",Toast.LENGTH_LONG).show()
                        401 -> Toast.makeText(requireContext(),"Unauthorized",Toast.LENGTH_LONG).show()
                        404 -> Toast.makeText(requireContext(),"Not Found",Toast.LENGTH_LONG).show()
                        500 -> Toast.makeText(requireContext(),"Internal Server Error",Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginRequest>, t: Throwable) {
                loading.stopLoader()
                Toast.makeText(requireContext(), "error",Toast.LENGTH_LONG).show()
                Log.d("error","${t}")
                flag = false
            }

        })
        return flag
    }

    private fun saveSharedPref(){
        val sharePref = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putString("token", Constant.token)
        editor.putString("name",Constant.name)
        editor.apply()
    }
    private fun navigateToDashBoard(){
        getFCMToken()
        val intent = Intent(this@LoginFragment.requireContext(),Home::class.java)
        startActivity(intent)
        activity?.finish()
    }

    companion object{
        @JvmStatic
        fun newInstance() = LoginFragment()
        private const val RC_SIGN_IN = 123
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
