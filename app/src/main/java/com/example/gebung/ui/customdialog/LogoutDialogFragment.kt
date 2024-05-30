package com.example.gebung.ui.customdialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.gebung.databinding.CustomDialogBinding
import com.example.gebung.databinding.LogoutDialogBinding
import com.example.gebung.ui.signin.SignInActivity

class LogoutDialogFragment: DialogFragment() {

    private lateinit var binding: LogoutDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LogoutDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        binding = LogoutDialogBinding.inflate(inflater, null, false)


        binding.btnLogout.setOnClickListener {
            val intent = Intent(activity, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        builder.setView(binding.root)

        return builder.create()
    }

}