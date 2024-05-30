package com.example.gebung.ui.customdialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.gebung.databinding.CustomDialogBinding
import com.example.gebung.databinding.SuccessDialogBinding

class SuccessDialogFragment: DialogFragment() {

    private lateinit var binding: SuccessDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SuccessDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        binding = SuccessDialogBinding.inflate(inflater, null, false)

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        builder.setView(binding.root)

        return builder.create()
    }

}