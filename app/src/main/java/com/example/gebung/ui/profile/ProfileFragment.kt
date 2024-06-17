package com.example.gebung.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.gebung.databinding.FragmentProfileBinding
import com.example.gebung.ui.about.AboutActivity
import com.example.gebung.ui.customdialog.LogoutDialogFragment
import com.example.gebung.ui.editprofile.EditProfileActivity
import com.example.gebung.ui.signin.SignInActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        binding.tvName.text = firebaseUser?.displayName.toString()
        actionListener()

        return binding.root
    }

    private fun actionListener() {
        binding.cvEditProfile.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.cvLogoutProfile.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
            val dialog = LogoutDialogFragment()
            dialog.show(requireActivity().supportFragmentManager, "CustomDialog")
    }

}