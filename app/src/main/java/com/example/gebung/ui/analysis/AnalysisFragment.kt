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
import java.util.Locale

class AnalysisFragment : Fragment() {

    private lateinit var binding: FragmentAnalysisBinding
    private lateinit var viewModel: AnalysisViewModel
    private lateinit var interpreter: Interpreter
    private var mCurrencyFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

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
                    setDataToChart(it)
                } else {
                    Log.d("AnalysisFragment", "No data available")
                }
            }
        }

        viewModel.previousPredictions.observe(viewLifecycleOwner) { predictions ->
            predictions?.let {
                if (it.isNotEmpty()) {
                    Log.d("AnalysisFragment", "Previous predictions: $it")
                    setDataToChart(viewModel.monthlyTotals.value ?: emptyList())
                }
            }
        }

        return binding.root
    }

    private fun loadModelFile(): MappedByteBuffer{
        val assetFileDescriptor = requireContext().assets.openFd("moving_average_model.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun setDataToChart(monthlyTotals: List<MonthlyTotal>) {
        val totalExpenseEntries = mutableListOf<Entry>()
        val predictionEntries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()

        monthlyTotals.forEachIndexed { index, total ->
            Log.d("AnalysisFragment", "Month: ${total.month}, Total: ${total.total}")
            totalExpenseEntries.add(Entry(index.toFloat(), total.total.toFloat()))
            labels.add(total.month ?: "unknown")
        }

        viewModel.previousPredictions.value?.forEachIndexed { index, total ->
            predictionEntries.add(Entry((monthlyTotals.size + index).toFloat(), total))
            labels.add("Prediksi ${index + 1}")
        }

        val expenseDataSet = LineDataSet(totalExpenseEntries, "Monthly Expenses").apply {
            color = Color.RED
            valueTextColor = Color.BLACK
        }

        val predictionDataSet = LineDataSet(predictionEntries, "Prediction").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
        }

        val lineData = LineData(predictionDataSet, expenseDataSet)
        binding.PredictChart.data = lineData
        binding.PredictChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        binding.PredictChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.PredictChart.xAxis.granularity = 1f
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