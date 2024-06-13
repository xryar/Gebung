package com.example.gebung.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gebung.databinding.ActivitySignUpBinding
import com.example.gebung.ui.signin.SignInActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        actionListener()

    }

    private fun actionListener() {

        binding.btnSignup.setOnClickListener {

            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty()){
                binding.emailEditText.error = "Please enter your email"
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.emailEditText.error = "Email not valid"
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()){
                binding.passwordEditText.error = "Please enter your password"
                binding.passwordEditText.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 8) {
                binding.passwordEditText.error = "Password must be at least 8 characters"
                binding.passwordEditText.requestFocus()
                return@setOnClickListener
            }

            registerFirebase(email, password)

        }

        binding.tvSignin.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

    }

    private fun registerFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Toast.makeText(
                        this,
                        "Register Success",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(
                        this,
                        "${it.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

}