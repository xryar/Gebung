package com.example.gebung.ui.analysis

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gebung.database.MonthlyTotal
import com.example.gebung.database.Prediction
import com.example.gebung.databinding.FragmentAnalysisBinding
import com.example.gebung.viewmodel.ViewModelFactory
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalysisFragment : Fragment() {

    private lateinit var binding: FragmentAnalysisBinding
    private lateinit var viewModel: AnalysisViewModel
    private lateinit var interpreter: Interpreter
    private var mCurrencyFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAnalysisBinding.inflate(inflater, container, false)

        interpreter = Interpreter(loadModelFile())

        val factory = ViewModelFactory(requireActivity().application)

        viewModel = ViewModelProvider(this, factory)[AnalysisViewModel::class.java]

        viewModel.monthlyTotals.observe(viewLifecycleOwner) { dataList ->
            dataList?.let {
                if (it.isNotEmpty()) {
                    Log.d("AnalysisFragment", "Data received: $it")
                    viewModel.updatePredictionsIfNeeded(it, interpreter)
                    setDataToChart(it, viewModel.databasePrediction.value ?: emptyList())
                } else {
                    Log.d("AnalysisFragment", "No data available")
                }
            }
        }

        viewModel.databasePrediction.observe(viewLifecycleOwner){ predictions ->
            predictions?.let {
                if (viewModel.monthlyTotals.value?.isNotEmpty() == true){
                    setDataToChart(viewModel.monthlyTotals.value!!, it)
                }
            }
        }

        binding.tvDate.text = getMonthName(Date())

        return binding.root
    }

    private fun getMonthName(date: Date): String {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun loadModelFile(): MappedByteBuffer{
        val assetFileDescriptor = requireContext().assets.openFd("moving_average_model.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


    private fun setDataToChart(monthlyTotals: List<MonthlyTotal>, predictions: List<Prediction>) {
        val totalExpenseEntries = mutableListOf<Entry>()
        val predictionEntries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()

        val monthlyTotalMap = monthlyTotals.associateBy { it.month }

        val allMonth = (monthlyTotals.map { it.month } + predictions.map { it.month }).distinct()

        allMonth.forEachIndexed { index, month ->
            val total = monthlyTotalMap[month]?.total?.toFloat() ?: 0f
            totalExpenseEntries.add(Entry(index.toFloat(), total))
            labels.add(month ?: "unknown")

            val prediction = predictions.find { it.month == month }?.predicted ?: 0f
            predictionEntries.add(Entry(index.toFloat(), prediction))
        }

        val expenseDataSet = LineDataSet(totalExpenseEntries, "Monthly Expenses").apply {
            color = Color.RED
            valueTextColor = Color.BLACK
            lineWidth = 2f
        }

        val predictionDataSet = LineDataSet(predictionEntries, "Prediction").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            lineWidth = 2f
        }

        val lineData = LineData(expenseDataSet, predictionDataSet)
        binding.PredictChart.data = lineData

        val xAxis = binding.PredictChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f

        binding.PredictChart.invalidate()

        val legend = binding.PredictChart.legend
        legend.form = Legend.LegendForm.LINE

        updatePredictionsDisplay(viewModel.previousPredictions.value ?: emptyList())
    }

    private fun updatePredictionsDisplay(predictions: List<Float>) {
        val formattedPredictions = predictions.map { mCurrencyFormat.format(it) }
        val formattedCurrency = formattedPredictions.joinToString()
        binding.tvMoney.text = formattedCurrency
    }


    override fun onDestroyView() {
        super.onDestroyView()
        interpreter.close()
    }

}