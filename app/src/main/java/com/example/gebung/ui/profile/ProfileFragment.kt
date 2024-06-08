package com.example.gebung.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gebung.databinding.FragmentProfileBinding
import com.example.gebung.ui.about.AboutActivity
import com.example.gebung.ui.customdialog.LogoutDialogFragment
import com.example.gebung.ui.editprofile.EditProfileActivity

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

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
        val dialog = LogoutDialogFragment()

        dialog.show(requireActivity().supportFragmentManager, "CustomDialog")
    }

}