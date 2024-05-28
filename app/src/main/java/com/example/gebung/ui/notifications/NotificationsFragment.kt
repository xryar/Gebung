package com.example.gebung.ui.notifications

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gebung.databinding.FragmentNotificationsBinding
import com.example.gebung.databinding.LogoutDialogBinding
import com.example.gebung.ui.about.AboutActivity
import com.example.gebung.ui.editprofile.EditProfileActivity
import com.example.gebung.ui.signup.SignUpActivity

class NotificationsFragment : Fragment() {

    private lateinit var binding : FragmentNotificationsBinding
    private lateinit var dialogBinding: LogoutDialogBinding

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        actionListener()

        return binding.root
    }

    private fun actionListener() {
        binding.cvEditProfile.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.cvAboutProfile.setOnClickListener {
            val intent = Intent(activity, AboutActivity::class.java)
            startActivity(intent)
        }

        binding.cvLogoutProfile.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        dialogBinding = LogoutDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnLogout.setOnClickListener {
            val intent = Intent(context, SignUpActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
            activity?.finish()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}