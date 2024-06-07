package com.example.gebung.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gebung.R
import com.example.gebung.databinding.FragmentHomeBinding
import com.example.gebung.ui.customdialog.CustomDialogFragment
import com.example.gebung.ui.history.HistoryActivity
import com.example.gebung.viewmodel.TransactionViewModel
import com.example.gebung.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var adapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val factory = ViewModelFactory(requireActivity().application)
        // Gunakan ViewModelFactory saat membuat instance dari TransactionViewModel
        viewModel = ViewModelProvider(this, factory).get(TransactionViewModel::class.java)
        adapter = HomeAdapter()
        showRecyclerView()
        showViewModel()
        actionListener()

        return binding.root
    }

    private fun showViewModel() {
        viewModel.getLastTransaction().observe(viewLifecycleOwner){
            adapter.getData(it)
        }
    }

    private fun showRecyclerView() {
        binding.apply {
            rvListTransaction.layoutManager = LinearLayoutManager(activity)
            rvListTransaction.setHasFixedSize(true)
            rvListTransaction.adapter = adapter
        }
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