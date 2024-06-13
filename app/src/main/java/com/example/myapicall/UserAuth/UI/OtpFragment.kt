package com.example.myapicall.UserAuth.UI

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.example.myapicall.R
import com.example.myapicall.UserAuth.Model.Constant
import com.example.myapicall.databinding.FragmentOtpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class OtpFragment : Fragment() {

    private val binding by lazy { FragmentOtpBinding.inflate(layoutInflater) }
    private var storedVerificationId : String? = ""
    lateinit var auth : FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = Bundle()

        storedVerificationId = Constant.storedVerificationId
        Log.d("storedVerificationId","$storedVerificationId")
        auth = Firebase.auth
        binding.otpbtn.setOnClickListener {
            verifyPhoneNumberWithCode(storedVerificationId, binding.otpET.text.toString())
        }
    }
    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("success", "signInWithCredential:success")

                    val user = task.result?.user
                    val transaction : FragmentTransaction = requireFragmentManager().beginTransaction()
                    transaction.replace(R.id.framelayout,LoginFragment.newInstance()).commit()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("failed", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

}