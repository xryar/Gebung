package com.example.gebung.ui.customdialog

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.gebung.database.Transaction
import com.example.gebung.databinding.CustomDialogBinding
import com.example.gebung.ui.home.SharedPreferencesHelper
import com.example.gebung.viewmodel.TransactionViewModel
import com.example.gebung.viewmodel.ViewModelFactory
import java.util.Calendar

class CustomDialogFragment: DialogFragment() {

    private lateinit var binding: CustomDialogBinding
    private lateinit var viewModel: TransactionViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        binding = CustomDialogBinding.inflate(inflater, null, false)

        val factory = ViewModelFactory(requireActivity().application)
        // Gunakan ViewModelFactory saat membuat instance dari TransactionViewModel
        viewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]

        val category = arguments?.getString(CATEGORY) ?: ""

        binding.edCategory.setText(category)
        val title = binding.edTitle.text
        binding.labelDate.setOnClickListener {
            showDatePickerDialog()
        }
        val price = binding.edPrice.text
        val radioGroup = binding.rgOptions
        val radioButtonIncome = binding.radioIncome
        val radioButtonExpense = binding.radioExpense

        binding.btnSave.setOnClickListener {
            val date = binding.textDate.text
            Log.d("CustomDialogFragment", "Saving transaction with date: $date")
            val type = when (radioGroup.checkedRadioButtonId) {
                radioButtonIncome.id -> "Income"
                radioButtonExpense.id -> "Expense"
                else -> "Expense" // Default to "Expense" if none is selected
            }

            val transaction = Transaction(
                description = title.toString(),
                category = category,
                date = date.toString(),
                amount = price.toString().toIntOrNull() ?: 0,
                type = type)
            viewModel.insert(transaction)
            SharedPreferencesHelper.addTransactionDate(requireContext(), date.toString())
            showSuccessDialog()
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        builder.setView(binding.root)

        return builder.create()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.textDate.text = selectedDate
        }, year, month, day)

        datePickerDialog.show()
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