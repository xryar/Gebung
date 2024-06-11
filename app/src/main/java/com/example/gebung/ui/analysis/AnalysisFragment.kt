package com.example.gebung.ui.analysis

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gebung.database.MonthlyTotal
import com.example.gebung.database.Transaction
import com.example.gebung.databinding.FragmentAnalysisBinding
import com.example.gebung.viewmodel.ViewModelFactory
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

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

        viewModel.monthlyTotals.observe(viewLifecycleOwner) { dataList ->
            dataList?.let {
                if (it.isNotEmpty()) {
                    Log.d("AnalysisFragment", "Data received: $it")
                    setDataToChart(it)
                } else {
                    Log.d("AnalysisFragment", "No data available")
                }
            }
        }

        return binding.root
    }


//    private fun displayChart(dataList: List<Transaction>) {
//        val spendingEntries = mutableListOf<Entry>()
//
//        dataList.forEachIndexed { index, data ->
//            spendingEntries.add(Entry(index.toFloat(), data.amount.toFloat()))
//        }
//
//        val spendingDataSet = LineDataSet(spendingEntries, "Spending").apply {
//            color = Color.RED
//            valueTextColor = Color.BLACK
//        }
//
//        binding.PredictChart.data = LineData(spendingDataSet)
//        binding.PredictChart.invalidate()
//
//    }

    private fun setDataToChart(monthlyTotals: List<MonthlyTotal>) {
        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()

        monthlyTotals.forEachIndexed { index, total ->
            Log.d("AnalysisFragment", "Month: ${total.month}, Total: ${total.total}")
            entries.add(Entry(index.toFloat(), total.total.toFloat()))
            labels.add(total.month ?: "unknown")
        }

        val dataSet = LineDataSet(entries, "Monthly Expenses").apply {
            color = Color.RED
            valueTextColor = Color.BLACK
        }

        val lineData = LineData(dataSet)
        binding.PredictChart.data = lineData
        binding.PredictChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        binding.PredictChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.PredictChart.xAxis.granularity = 1f
        binding.PredictChart.invalidate()

    }

}