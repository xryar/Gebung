package com.example.gebung.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gebung.MainActivity
import com.example.gebung.R
import com.example.gebung.databinding.ActivitySignInBinding
import com.example.gebung.ui.signup.SignUpActivity
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        actionListener()

    }

    private fun actionListener() {

        binding.btnSingin.setOnClickListener {

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

            loginFirebase(email, password)

        }

        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loginFirebase(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Login Success",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this, MainActivity::class.java)
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

    override fun onBackPressed() {
        super.onBackPressed()
        // Intent for going back to home screen
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


}