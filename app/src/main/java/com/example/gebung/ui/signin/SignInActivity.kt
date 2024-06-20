package com.example.gebung.ui.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.gebung.MainActivity
import com.example.gebung.R
import com.example.gebung.databinding.ActivitySignInBinding
import com.example.gebung.ui.signup.SignUpActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        actionListener()
        binding.ivGoogle.setOnClickListener {
            signInGoogle()
        }
        binding.tvSigninForgot.setOnClickListener {
            showForgotPasswordDialog()
        }

    }

    private fun signInGoogle() {
        val credentialManager = CredentialManager.create(this)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.your_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = this@SignInActivity
                )
                handleSignInGoogle(result)
            }catch (e: GetCredentialException){
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun handleSignInGoogle(result: GetCredentialResponse) {
        when (val credential = result.credential){
            is CustomCredential ->{
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    }catch (e: GoogleIdTokenParsingException){
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                }else{
                    Log.d(TAG, "Unexpected type of credential")
                }
            }
            else ->{
                Log.d(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        showLoading(true)
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user)
                    showLoading(false)
                }else{
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                    showLoading(false)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?){
        if (currentUser != null){
            AlertDialog.Builder(this, R.style.CustomAlertDialogTheme).apply {
                setTitle("Yeah!")
                setMessage("You have successfully logged in")
                setPositiveButton("Next"){_, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                    val sharedPref = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
                    with(sharedPref.edit()){
                        putBoolean("Finished", true)
                        apply()
                    }
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        }
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
        showLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user)
                    showLoading(false)
                } else {
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                    showLoading(false)
                }
            }
    }

    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
        moveTaskToBack(true)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        val justSignedUp = intent.getBooleanExtra("JustSignUp", false)

        if (!justSignedUp){
            updateUI(currentUser)
        }
    }

    private fun showForgotPasswordDialog(){
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
        builder.setTitle("Forgot Password")
        val view = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
        val emailEditText = view.findViewById<EditText>(R.id.etEmail)
        builder.setView(view)
        builder.setPositiveButton("Reset Password"){ _, _ ->
            val email = emailEditText.text.toString()
            if (email.isEmpty()){
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            resetPassword(email)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun resetPassword(email: String){
        showLoading(true)
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(this, "Reset password email sent", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }else{
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object{
        private const val TAG = "SignInActivity"
    }


}