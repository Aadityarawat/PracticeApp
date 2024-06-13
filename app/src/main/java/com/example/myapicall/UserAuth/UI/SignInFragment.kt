package com.example.myapicall.UserAuth.UI

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.myapicall.Loader
import com.example.myapicall.R
import com.example.myapicall.UserAuth.Model.Register
import com.example.myapicall.UserAuth.Model.RegisterResponse
import com.example.myapicall.UserAuth.Network.RestApi
import com.example.myapicall.UserAuth.Network.Service
import com.example.myapicall.databinding.FragmentLogin2Binding
import com.example.myapicall.databinding.FragmentSignInBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create


class SignInFragment : Fragment() {
    lateinit var loading : Loader
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var transaction : FragmentTransaction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        loading = Loader(this)
        firstnameFocusListner()
        lastnameFocusListner()
        emailFocusListner()
        passFocusListner()

        binding.signback.setOnClickListener {
            moveToLogin()
        }

        binding.signlogin.setOnClickListener {
            moveToLogin()
        }

        binding.signbtn.setOnClickListener {
            checkValidation()
        }
        return binding.root
    }

    private fun moveToLogin() {
        transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.framelayout,LoginFragment()).commit()
        fragmentManager?.popBackStack("login",1)
    }

    private fun checkValidation() {
        val emailtext = binding.signemailET.text.toString()
        val passtext = binding.signpassET.text.toString()
        val firstnametext = binding.signfirstnameET.text.toString()
        val lastnametext = binding.siglastnameET.text.toString()

        if (emailtext.isEmpty() || passtext.isEmpty() || firstnametext.isEmpty() || lastnametext.isEmpty()){
            if(emailtext.isEmpty()){
                binding.emaillContainer.boxStrokeColor = Color.RED
                binding.emaillContainer.helperText = "Can not be Empty"
            }
             if(passtext.isEmpty()){
                binding.passwordContainer.boxStrokeColor = Color.RED
                binding.passwordContainer.helperText = "Can not be Empty"
            }
             if (firstnametext.isEmpty()){
                binding.firstnameContainer.boxStrokeColor = Color.RED
                binding.firstnameContainer.helperText = "Can not be Empty"
            }
             if (lastnametext.isEmpty()){
                binding.lastnameContainer.boxStrokeColor = Color.RED
                binding.lastnameContainer.helperText = "Can not be Empty"
            }
        }
        else if(validEmail() == null && validFirstName() == null && validLastName() == null && validpass() == null){
            postSign()
        }
        else{
            Toast.makeText(requireContext(),"Please Provide Valid Details", Toast.LENGTH_LONG).show()
        }
    }

    private fun lastnameFocusListner() {
        binding.siglastnameET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                binding.lastnameContainer.helperText = validLastName()
            }
        }
    }

    private fun validLastName(): String? {

        val lastnameText = binding.siglastnameET.text.toString()
        if (lastnameText.isEmpty()){
            return "Can not be Empty"
        }
        return null
    }

    private fun emailFocusListner() {
        binding.signemailET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                binding.emaillContainer.helperText = validEmail()
            }
        }
    }

    private fun validEmail(): String? {

        val emailText = binding.signemailET.text.toString()
        if (emailText.isEmpty()){
            return "Can not be Empty"
        }
        return null
    }

    private fun passFocusListner() {
        binding.signpassET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                binding.passwordContainer.helperText = validpass()
            }
        }
    }

    private fun validpass(): String? {

        val passText = binding.signpassET.text.toString()
        if (passText.isEmpty()){
            return "Can not be Empty"
        }
        return null
    }

    private fun firstnameFocusListner() {
        binding.signfirstnameET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                binding.firstnameContainer.helperText = validFirstName()
            }
        }
    }

    private fun validFirstName(): String? {

        val firstnameText = binding.signfirstnameET.text.toString()
        if (firstnameText.isEmpty()){
            return "Can not be Empty"
        }
        return null
    }

    private fun postSign() : Boolean{
        var flag = false
        val retrofit = RestApi.getRetrofitInstance()
        val data = Register(
            first_name = binding.signfirstnameET.text.toString(),
            last_name = binding.siglastnameET.text.toString(),
            email = binding.signemailET.text.toString(),
            password = binding.signpassET.text.toString(),
            register_type = "native",
            device_type = "Android",
            role_type = "family"
        )
        val service = retrofit.create(Service::class.java).postRegister(data)
        loading.startloader()
        service.enqueue(object : Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if(response.isSuccessful){
                    loading.stopLoader()
                    Toast.makeText(requireContext(), "${response.body()?.status} success",Toast.LENGTH_LONG).show()
                    navigateToLoginfrag()
                }else{
                    loading.stopLoader()
                    when (response.code()){
                        400 -> Toast.makeText(requireContext(),"Bad Request",Toast.LENGTH_LONG).show()
                        401 -> Toast.makeText(requireContext(),"Unauthorized",Toast.LENGTH_LONG).show()
                        404 -> Toast.makeText(requireContext(),"Not Found",Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                loading.stopLoader()
                Toast.makeText(requireContext(), "error",Toast.LENGTH_LONG).show()
                Log.d("error","${t}")
                flag = false
            }

        })
        return flag
    }

    private fun navigateToLoginfrag(){
        transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.framelayout, LoginFragment()).commit()
        fragmentManager?.popBackStack("login",1)
    }
    companion object {

        @JvmStatic
        fun newInstance() = SignInFragment()
    }

}