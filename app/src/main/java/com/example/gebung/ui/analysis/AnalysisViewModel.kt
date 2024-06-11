package com.example.gebung.ui.analysis

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gebung.database.MonthlyTotal
import com.example.gebung.database.Transaction
import com.example.gebung.database.TransactionDao
import com.example.gebung.database.TransactionRoomDatabase

class AnalysisViewModel(application: Application) : ViewModel() {

    private val dataDao: TransactionDao = TransactionRoomDatabase.getDatabase(application).transactionDao()
    val allData: LiveData<List<Transaction>> = dataDao.getAllTransactionASC()

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

        _previousPredictions.value = mutableListOf()
    }

    fun addPrediction(predictions: List<Float>){
        val updatedPredictions = _previousPredictions.value ?: mutableListOf()
        updatedPredictions.addAll(predictions)
        _previousPredictions.value = updatedPredictions
    }


}