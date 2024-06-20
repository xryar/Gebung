package com.example.gebung.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gebung.database.MonthlyTotal
import com.example.gebung.database.Prediction
import com.example.gebung.database.PredictionDao
import com.example.gebung.database.TransactionDao
import com.example.gebung.database.TransactionRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.time.YearMonth

class AnalysisViewModel(application: Application) : ViewModel() {

    private val predictionDao: PredictionDao = TransactionRoomDatabase.getDatabase(application).predictionDao()

    private val dataDao: TransactionDao = TransactionRoomDatabase.getDatabase(application).transactionDao()

    val monthlyTotals: LiveData<List<MonthlyTotal>> = dataDao.getMonthlyTotals("Expense")

    val databasePrediction: LiveData<List<Prediction>> = predictionDao.getAllPredictions()

    private val _previousPredictions = MutableLiveData<MutableList<Float>>()
    val previousPredictions: LiveData<MutableList<Float>> = _previousPredictions


    private fun addPrediction(predictions: List<Float>) {
        val updatedPredictions = _previousPredictions.value ?: mutableListOf()
        updatedPredictions.addAll(predictions)
        _previousPredictions.value = updatedPredictions
        _previousPredictions.postValue(updatedPredictions)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updatePredictionsIfNeeded(monthlyTotals: List<MonthlyTotal>, interpreter: Interpreter) {
        _previousPredictions.value = mutableListOf()
        val predictions = predictNextMonthTotals(monthlyTotals, interpreter)
        addPrediction(predictions.toList())
        savePredictionsToDatabase(monthlyTotals, predictions.toList())
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
            return FloatArray(0)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun savePredictionsToDatabase(monthlyTotals: List<MonthlyTotal>, predictions: List<Float>){
        viewModelScope.launch(Dispatchers.IO) {
            val lastMonth = monthlyTotals.lastOrNull()?.month
            val lastYearMonth = YearMonth.parse(lastMonth)

            predictions.forEachIndexed{index, prediction ->
                val nextMonth = lastYearMonth?.plusMonths(index.toLong()+1)
                val monthString = nextMonth.toString()

                val count = predictionDao.countMonthTotal(monthString)
                if (count == 0){
                    val newMonthlyTotal = Prediction(month = nextMonth.toString(), predicted = prediction)
                    predictionDao.insert(newMonthlyTotal)
                }else{
                    Log.d("AnalysisViewModel", "Record for month $monthString already exist, skipping insert.")
                }
            }
        }
    }
}