package com.example.gebung.ui.analysis

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gebung.database.MonthlyTotal
import com.example.gebung.database.TransactionDao
import com.example.gebung.database.TransactionRoomDatabase
import org.tensorflow.lite.Interpreter

class AnalysisViewModel(application: Application) : ViewModel() {

    private val dataDao: TransactionDao = TransactionRoomDatabase.getDatabase(application).transactionDao()

    val monthlyTotals: LiveData<List<MonthlyTotal>> = dataDao.getMonthlyTotals("Expense")

    private val _previousPredictions = MutableLiveData<MutableList<Float>>()
    val previousPredictions: LiveData<MutableList<Float>> = _previousPredictions

    init {

        monthlyTotals.observeForever { monthlyTotals ->
            if (monthlyTotals != null) {
                Log.d("AnalysisViewModel", "Monthly totals: $monthlyTotals")
            } else {
                Log.d("AnalysisViewModel", "No data received")
            }
        }
    }

    private fun addPrediction(predictions: List<Float>) {
        val updatedPredictions = _previousPredictions.value ?: mutableListOf()
        updatedPredictions.addAll(predictions)
        _previousPredictions.value = updatedPredictions
        _previousPredictions.postValue(updatedPredictions)
    }

    fun updatePredictionsIfNeeded(monthlyTotals: List<MonthlyTotal>, interpreter: Interpreter) {
        _previousPredictions.value = mutableListOf()
        val predictions = predictNextMonthTotals(monthlyTotals, interpreter)
        addPrediction(predictions.toList())
    }

    private fun predictNextMonthTotals(monthlyTotals: List<MonthlyTotal>, interpreter: Interpreter): FloatArray{
        val inputArray = FloatArray(monthlyTotals.size) {i -> monthlyTotals[i].total.toFloat()}
        val inputBuffer = arrayOf(inputArray)
        val outputBuffer = Array(1){ FloatArray(1)}

        Log.d("AnalysisFragment", "Input shape: ${inputBuffer.size}x${inputBuffer[0].size}")
        Log.d("AnalysisFragment", "Output shape: ${outputBuffer.size}x${outputBuffer[0].size}")

        try {
            interpreter.run(inputBuffer, outputBuffer)
            Log.d("AnalysisFragment", "Prediction successful: ${outputBuffer[0].contentToString()}")
            return outputBuffer[0]
        } catch (e: IllegalArgumentException) {
            Log.e("AnalysisFragment", "Error during prediction: ${e.message}")
            return FloatArray(0) // Return an empty array or handle it accordingly
        }
    }

}