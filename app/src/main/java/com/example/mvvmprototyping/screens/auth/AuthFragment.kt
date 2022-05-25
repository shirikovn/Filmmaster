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

        binding.textButton.setOnClickListener {
            if(binding.textButton.text == "LOGIN") {
                signIn(binding.emailField.text.toString(), binding.passwordField.text.toString())
            } else {
                if(binding.passwordField.text.toString() == (binding.passwordField2.text.toString())) {
                    createAccount(
                        binding.emailField.text.toString(),
                        binding.passwordField.text.toString()
                    )
                } else {
                    Toast.makeText(requireContext(), "WRONG PASSWORD", Toast.LENGTH_LONG).show()
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
            }else{
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
            //TODO
        }

        return binding.root
    }

    fun createAccount(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
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

                }
        }
    }


    fun signIn(email: String, password: String){
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
            }
        }
    }
}