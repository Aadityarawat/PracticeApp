package com.example.myapicall.UserAuth.UI

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentTransaction
import com.example.myapicall.R
import com.example.myapicall.UserAuth.Model.Constant
import com.example.myapicall.databinding.FragmentForgotPassBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit


class ForgotPassFragment : Fragment() {
    private val binding by lazy { FragmentForgotPassBinding.inflate(layoutInflater) }
    lateinit var mAuth : FirebaseAuth
    private var storedVerificationId : String? = ""
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    )= binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        callBacksFuction()
        validateInputType()

        binding.forgotpassbtn.setOnClickListener {
            verifyPhoneNumberWithCode(storedVerificationId, binding.forgototpET.text.toString())
        }

    }

    private fun callBacksFuction() {
        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

//                val transaction = requireFragmentManager().beginTransaction()
//                transaction.replace(R.id.framelayout,OtpFragment()).commit()
                storedVerificationId = storedVerificationId as String

            }
        }
        // [END phone_auth_callbacks]
    }
    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }

    private fun validateInputType() {

        binding.forgotphonenoET.addTextChangedListener {
            val text = binding.forgotphonenoET.text.toString()
            val size = text.length
            Log.d("phone no text","$text")
            if (size == 10){
                    // if the text field is not empty we are calling our
                    // send OTP method for getting OTP from Firebase.
                    val phone = "+91"+ text
                    Log.d("phone no","$phone")
                    sendVerificationCode(phone)
            }
        }

    }


    private fun sendVerificationCode(phone: String) {
        val phoneAuthOption = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOption)
        //counter()
        mAuth.setLanguageCode("fr")
    }

    private fun counter() {

        for (i in 30..0){
            Handler(Looper.getMainLooper()).postDelayed({
                binding.textView3.text = i.toString()
            },1000)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.framelayout,LoginFragment.newInstance())?.commit()

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
    // [END sign_in_with_phone]

    private fun updateUI(user: FirebaseUser? = mAuth.currentUser) {
    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
    }


    private fun getFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful){
                Log.d("FCMToken","Fetching FCM registration token failed")
                return@addOnCompleteListener
            }
            val token: String = task.result
            Log.d("FCNToken", "onComplete: $token")
            saveFCMToken(token)
        }
    }

    private fun saveFCMToken(token: String){
        Constant.fcm_Token = token

        val sharedPreferences = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("fcmToken", token)
        editor.apply()
    }
}