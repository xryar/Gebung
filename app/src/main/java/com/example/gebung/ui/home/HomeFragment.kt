package com.example.gebung.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gebung.R
import com.example.gebung.database.Transaction
import com.example.gebung.databinding.CustomDialogBinding
import com.example.gebung.databinding.FragmentHomeBinding
import com.example.gebung.ui.customdialog.CustomDialogFragment
import com.example.gebung.ui.history.HistoryActivity
import com.example.gebung.viewmodel.TransactionViewModel
import com.example.gebung.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: TransactionViewModel

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

        binding.btnShop.setOnClickListener {
            showDialog(getString(R.string.shop))
        }

        binding.btnFood.setOnClickListener {
            showDialog(getString(R.string.food))
        }

        binding.btnTransport.setOnClickListener {
            showDialog(getString(R.string.transport))
        }

        binding.btnHealth.setOnClickListener {
            showDialog(getString(R.string.health))
        }

        binding.btnOther.setOnClickListener {
            showDialog(getString(R.string.other))
        }

        binding.tvHistory.setOnClickListener {
            val intent = Intent(activity, HistoryActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showDialog(category: String) {
        val dialog = CustomDialogFragment.newInstance(category)

        dialog.show(requireActivity().supportFragmentManager, "CustomDialog")

    }
}