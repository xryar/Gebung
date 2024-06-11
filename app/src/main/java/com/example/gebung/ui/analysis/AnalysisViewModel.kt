package com.example.gebung.ui.analysis

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.gebung.database.MonthlyTotal
import com.example.gebung.database.Transaction
import com.example.gebung.database.TransactionDao
import com.example.gebung.database.TransactionRoomDatabase

class AnalysisViewModel(application: Application) : ViewModel() {

    private val dataDao: TransactionDao = TransactionRoomDatabase.getDatabase(application).transactionDao()

    val monthlyTotals: LiveData<List<MonthlyTotal>> = dataDao.getMonthlyTotals("Expense")

    init {
        monthlyTotals.observeForever { monthlyTotals ->
            if (monthlyTotals != null) {
                Log.d("AnalysisViewModel", "Monthly totals: $monthlyTotals")
            } else {
                Log.d("AnalysisViewModel", "No data received")
            }
        }
    }


}