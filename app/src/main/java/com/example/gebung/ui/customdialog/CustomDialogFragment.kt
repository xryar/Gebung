package com.example.gebung.ui.customdialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.gebung.database.Transaction
import com.example.gebung.databinding.CustomDialogBinding
import com.example.gebung.viewmodel.TransactionViewModel
import com.example.gebung.viewmodel.ViewModelFactory

class CustomDialogFragment: DialogFragment() {

    private lateinit var binding: CustomDialogBinding
    private lateinit var viewModel: TransactionViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        binding = CustomDialogBinding.inflate(inflater, null, false)

        val factory = ViewModelFactory(requireActivity().application)
        // Gunakan ViewModelFactory saat membuat instance dari TransactionViewModel
        viewModel = ViewModelProvider(this, factory).get(TransactionViewModel::class.java)

        val category = arguments?.getString(CATEGORY) ?: ""

        binding.edCategory.setText(category)
        val title = binding.edTitle.text
        val date = binding.edDate.text
        val price = binding.edPrice.text
        val radioGroup = binding.rgOptions
        val radioButtonIncome = binding.radioIncome
        val radioButtonExpense = binding.radioExpense

        binding.btnSave.setOnClickListener {

            val type = when (radioGroup.checkedRadioButtonId) {
                radioButtonIncome.id -> "Income"
                radioButtonExpense.id -> "Expense"
                else -> "Expense" // Default to "Expense" if none is selected
            }

            val transaction = Transaction(
                title = title.toString(),
                category = category,
                date = date.toString(),
                nominal = price.toString().toIntOrNull() ?: 0,
                type = type)
            viewModel.insert(transaction)
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