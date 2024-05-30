package com.example.gebung.ui.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gebung.R
import com.example.gebung.databinding.CustomDialogBinding
import com.example.gebung.databinding.FragmentHomeBinding
import com.example.gebung.ui.history.HistoryActivity

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var dialogBinding: CustomDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        actionListener()

        return binding.root
    }

    private fun actionListener() {

        binding.btnAdd.setOnClickListener {
            showDialog()
        }

        binding.btnFood.setOnClickListener {
            showDialog()
            dialogBinding.edCategory.setText("Food")
        }

        binding.btnTransport.setOnClickListener {
            showDialog()
            dialogBinding.edCategory.setText("Transport")
        }

        binding.btnGift.setOnClickListener {
            showDialog()
            dialogBinding.edCategory.setText("Gift")
        }

        binding.btnSports.setOnClickListener {
            showDialog()
            dialogBinding.edCategory.setText("Sports")
        }

        binding.tvHistory.setOnClickListener {
            val intent = Intent(activity, HistoryActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showDialog() {
        dialogBinding = CustomDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(this.requireContext())
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnSave.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }


}