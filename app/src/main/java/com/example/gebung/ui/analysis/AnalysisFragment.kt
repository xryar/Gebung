package com.example.gebung.ui.analysis

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.gebung.database.Transaction
import com.example.gebung.database.TransactionRoomDatabase
import com.example.gebung.databinding.FragmentAnalysisBinding
import com.example.gebung.viewmodel.TransactionViewModel
import com.example.gebung.viewmodel.ViewModelFactory
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch

class AnalysisFragment : Fragment() {

    private lateinit var binding: FragmentAnalysisBinding
    private lateinit var viewModel: AnalysisViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAnalysisBinding.inflate(inflater, container, false)

        val factory = ViewModelFactory(requireActivity().application)

        viewModel = ViewModelProvider(this, factory)[AnalysisViewModel::class.java]
        viewModel.allData.observe(viewLifecycleOwner) { dataList ->
            dataList?.let {
                displayChart(it)
            }
        }

        return binding.root
    }


    private fun displayChart(dataList: List<Transaction>) {
        val spendingEntries = mutableListOf<Entry>()

        dataList.forEachIndexed { index, data ->
            spendingEntries.add(Entry(index.toFloat(), data.nominal.toFloat()))
        }

        val spendingDataSet = LineDataSet(spendingEntries, "Spending").apply {
            color = Color.RED
            valueTextColor = Color.BLACK
        }

        binding.PredictChart.data = LineData(spendingDataSet)
        binding.PredictChart.invalidate()

    }
}