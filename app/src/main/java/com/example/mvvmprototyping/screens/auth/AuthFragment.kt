package com.example.mvvmprototyping.screens.auth

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mvvmprototyping.databinding.FragmentAuthBinding
import com.example.mvvmprototyping.screens.base.BaseFragment
import com.example.mvvmprototyping.screens.base.BaseScreen
import com.example.mvvmprototyping.screens.base.BaseViewModel
import com.example.mvvmprototyping.screens.base.screenViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.parcelize.Parcelize
import kotlin.random.Random


private lateinit var auth: FirebaseAuth
private lateinit var binding: FragmentAuthBinding

class AuthFragment(

) : BaseFragment() {

    override val viewModel by screenViewModel<AuthViewModel>()

    class Screen: BaseScreen

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAuthBinding.inflate(inflater, container, false)

        auth = Firebase.auth
        Firebase.auth

        if(auth.currentUser != null){
            viewModel.launchChooseProject()
        }

//        binding.goToForgotPassword.setOnClickListener{
//            binding.passwordFieldLayout.visibility = View.GONE
//
//            val email = binding.emailField.text.toString()
//
//            if(binding.emailField.text.toString() != "") {
//                Firebase.auth.sendPasswordResetEmail(email)
//            }
//        }

        binding.textButton.setOnClickListener {
            if(binding.emailField.text.toString() != "" && binding.passwordField.text.toString() != "") {
            if(binding.textButton.text == "LOGIN") {
                    signIn(
                        binding.emailField.text.toString(),
                        binding.passwordField.text.toString()
                    )
            }
            else {
                if (binding.passwordField.text.toString() == (binding.passwordField2.text.toString())) {
                    createAccount(
                        binding.emailField.text.toString(),
                        binding.passwordField.text.toString()
                    )
                } else {
                    Toast.makeText(requireContext(), "WRONG PASSWORD", Toast.LENGTH_LONG).show()
                }
            }
            } else {
                Toast.makeText(requireContext(), "INPUT DATA", Toast.LENGTH_LONG).show()
            }

            if(binding.goToForgotPassword.text.toString() == "SIGN IN"){
                val email = binding.emailField.text.toString()
                Toast.makeText(requireContext(), "EMAIL SEND", Toast.LENGTH_LONG).show()
                if(email != "") {
                    Firebase.auth.sendPasswordResetEmail(email)
                    Toast.makeText(requireContext(), "EMAIL SEND", Toast.LENGTH_LONG).show()
                }
            }

            //Log.d("TAGTAG", auth.currentUser!!.email.toString())
            //viewModel.launchChooseProject(auth.currentUser!!.email.toString())

        }

        binding.goToRegisterButton.setOnClickListener {
            if (binding.goToRegisterButton.text == "NEW USER? SIGN UP"){
                binding.repeatPasswordFieldLayout.visibility = View.VISIBLE
                binding.passwordField2.visibility = View.VISIBLE
                binding.goToRegisterButton.text = "SIGN IN"
                binding.textButton.text = "REGISTER"
            }


            else{
                binding.repeatPasswordFieldLayout.visibility = View.GONE
                binding.passwordField2.visibility = View.GONE
                binding.goToRegisterButton.text = "NEW USER? SIGN UP"
                binding.textButton.text = "LOGIN"
            }

        }

        binding.continueWithGoogle.setOnClickListener {
            //TODO
        }

        binding.goToForgotPassword.setOnClickListener {
            if(binding.textButton.text == "SEND EMAIL"){
                binding.passwordFieldLayout.visibility = View.VISIBLE
                binding.textButton.text = "LOGIN"
                binding.goToForgotPassword.text = "forgot password"
            } else {
                binding.passwordFieldLayout.visibility = View.GONE
                binding.textButton.text = "SEND EMAIL"
                binding.goToForgotPassword.text = "SIGN IN"
            }
        }

        return binding.root
    }

    fun createAccount(email: String, password: String){
        binding.progressBarAuth.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                binding.progressBarAuth.visibility = View.GONE
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAGTAG", "createUserWithEmail:success")
                    val user = auth.currentUser
                    if(user != null) {
//                        Firebase.firestore.collection("project").document(user.uid).collection("testProject1").add(
//                            hashMapOf(
//                                "Name" to "TestProject",
//                                "Date" to 23,
//                                "Country" to "USA"
//                            )
//                        )
                        viewModel.launchChooseProject()
                    }


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAGTAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    binding.progressBarAuth.visibility = View.GONE

                }
        }
    }


    fun signIn(email: String, password: String){
        binding.progressBarAuth.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
               if (task.isSuccessful) {
                 // Sign in success, update UI with the signed-in user's information
                  Log.d("TAGTAG", "signInWithEmail:success")

                   viewModel.launchChooseProject()

             } else {
                // If sign in fails, display a message to the user.
                Log.w("TAGTAG", "signInWithEmail:failure", task.exception)
                Toast.makeText(requireContext(), "Authentication failed.",
                    Toast.LENGTH_SHORT).show()
                   binding.progressBarAuth.visibility = View.GONE
            }
        }
    }
}