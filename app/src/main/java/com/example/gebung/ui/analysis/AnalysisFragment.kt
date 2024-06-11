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

class AnalysisFragment : Fragment() {

    private lateinit var binding: FragmentAnalysisBinding
    private lateinit var viewModel: AnalysisViewModel
    private lateinit var interpreter: Interpreter

    // This property is only valid between onCreateView and
    // onDestroyView.

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
                    setDataToChart(it)
                } else {
                    Log.d("AnalysisFragment", "No data available")
                }
            }
        }

        viewModel.previousPredictions.observe(viewLifecycleOwner){ predictions ->
            predictions?.let {
                if (it.isNotEmpty()){
                    Log.d("AnalysisFragment", "Previous predictions : $it")
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

    private fun predictNextMonthTotals(monthlyTotals: List<MonthlyTotal>): FloatArray{
        val inputArray = FloatArray(monthlyTotals.size) {i -> monthlyTotals[i].total.toFloat()}
        val inputBuffer = arrayOf(inputArray)
        val outputBuffer = Array(1){ FloatArray(1)}

        interpreter.run(inputBuffer, outputBuffer)
        return outputBuffer[0]
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

        //Menambahkan hasil prediksi lama ke chart
        viewModel.previousPredictions.value?.forEachIndexed{ index, total ->
            predictionEntries.add(Entry((monthlyTotals.size + index).toFloat(), total))
            labels.add("Prediksi ${index + 1}")
        }

        //Prediksi total bulan berikutnya
        val predictTotal = predictNextMonthTotals(monthlyTotals)
        predictTotal.forEachIndexed{ index, total ->
            predictionEntries.add(Entry((monthlyTotals.size + index).toFloat(), total))
            labels.add("Prediksi ${index + 1 + (viewModel.previousPredictions.value?.size ?: 0)}")
        }

            viewModel.addPrediction(predictTotal.toList())

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        interpreter.close()
    }

}