package com.example.gebung.ui.customdialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.gebung.databinding.FragmentLimitDialogBinding

class LimitDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentLimitDialogBinding
    private var listener: LimitSetListener? = null

    interface LimitSetListener {
        fun onLimitSet(limit: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        binding = FragmentLimitDialogBinding.inflate(inflater, null, false)

        binding.btnSave.setOnClickListener {
            val limit = binding.edLimit.text.toString().toIntOrNull()
            if (limit != null){
                listener?.onLimitSet(limit)
                dismiss()
            }else{
                Toast.makeText(context, "Please enter a valid limit", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        builder.setView(binding.root)

        return builder.create()

    }

    fun setLimitListener(listener: LimitSetListener){
        this.listener = listener
    }

}