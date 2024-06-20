package com.example.gebung.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gebung.databinding.FragmentProfileBinding
import com.example.gebung.ui.about.AboutActivity
import com.example.gebung.ui.customdialog.LogoutDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

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
        binding.cvAbout.setOnClickListener {
            val intent = Intent(activity, AboutActivity::class.java)
            startActivity(intent)
        }

        binding.cvContactUs.setOnClickListener {
            sendEmail()
        }

        binding.cvLogoutProfile.setOnClickListener {
            showDialog()
        }
    }

    private fun sendEmail() {
        val recipient = "a211d4ky4433@bangkit.academy"
        val subject = "Contact us / Feedback"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        startActivity(intent)
    }

    private fun showDialog() {
            val dialog = LogoutDialogFragment()
            dialog.show(requireActivity().supportFragmentManager, "CustomDialog")
    }

}