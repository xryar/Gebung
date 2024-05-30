package com.example.gebung.ui.customdialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.gebung.databinding.CustomDialogBinding

class CustomDialogFragment: DialogFragment() {

    private lateinit var binding: CustomDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CustomDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        binding = CustomDialogBinding.inflate(inflater, null, false)

        val category = arguments?.getString(CATEGORY) ?: ""

        binding.edCategory.setText(category)

        binding.btnSave.setOnClickListener {
            showSuccessDialog()
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        builder.setView(binding.root)

        return builder.create()
    }

    private fun showSuccessDialog() {
        val dialog = SuccessDialogFragment()

        dialog.show(requireActivity().supportFragmentManager, "success_dialog")
    }

    companion object{
        private const val CATEGORY = "category"

        fun newInstance(category: String): CustomDialogFragment {
            val fragment = CustomDialogFragment()
            val args = Bundle()
            args.putString(CATEGORY, category)
            fragment.arguments = args
            return fragment
        }
    }

}