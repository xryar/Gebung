package com.example.gebung.ui.customdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.gebung.databinding.FragmentLimitDialogBinding

class LimitDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentLimitDialogBinding
    private var listener: LimitSetListener? = null

    interface LimitSetListener {
        fun onLimitSet(limit: Int)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentLimitDialogBinding.inflate(inflater, container, false)

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

        return binding.root
    }

    fun setLimitListener(listener: LimitSetListener){
        this.listener = listener
    }

}