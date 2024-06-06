package com.example.gebung.ui.analysis

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gebung.databinding.FragmentAnalysisBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentAnalysisBinding

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAnalysisBinding.inflate(inflater, container, false)

        //Data Dummy Tod
        val incomeEntries = listOf(
            Entry(0f, 0f), Entry(1f, 1000f), Entry(2f, 2000f), Entry(3f, 1500f),
            Entry(4f, 3000f), Entry(5f, 2500f), Entry(6f, 4000f), Entry(7f, 1000f),
            Entry(8f, 1500f), Entry(9f, 2000f), Entry(10f, 3500f), Entry(11f, 3000f)
        )

        val spendingEntries = listOf(
            Entry(0f, 0f), Entry(1f, 500f), Entry(2f, 3000f), Entry(3f, 1000f),
            Entry(4f, 2500f), Entry(5f, 3500f), Entry(6f, 2000f), Entry(7f, 1500f),
            Entry(8f, 1000f), Entry(9f, 500f), Entry(10f, 2500f), Entry(11f, 1500f)
        )

        val incomeDataSet = LineDataSet(incomeEntries, "Income").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
        }

        val spendingDataSet = LineDataSet(spendingEntries, "Spending").apply {
            color = Color.RED
            valueTextColor = Color.BLACK
        }

        binding.PredictChart.data = LineData(incomeDataSet, spendingDataSet)
        binding.PredictChart.invalidate()

        val legend = binding.PredictChart.legend
        legend.form = Legend.LegendForm.LINE

        return binding.root
    }
}