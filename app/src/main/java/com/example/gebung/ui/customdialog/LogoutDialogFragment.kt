package com.example.gebung.ui.customdialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.gebung.databinding.LogoutDialogBinding
import com.example.gebung.ui.signin.SignInActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class LogoutDialogFragment: DialogFragment() {

    private lateinit var binding: LogoutDialogBinding
    private lateinit var authGoogle: FirebaseAuth

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        binding = LogoutDialogBinding.inflate(inflater, null, false)

        authGoogle = Firebase.auth


        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                val credentialManager = CredentialManager.create(requireActivity().applicationContext)

                authGoogle.signOut()
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            }
            startActivity(Intent(requireActivity(), SignInActivity::class.java))
            requireActivity().finish()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        builder.setView(binding.root)

        return builder.create()
    }

}