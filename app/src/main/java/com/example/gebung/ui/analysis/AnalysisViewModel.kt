package com.example.gebung.ui.analysis

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gebung.database.Transaction
import com.example.gebung.database.TransactionDao
import com.example.gebung.database.TransactionRoomDatabase
import kotlinx.coroutines.launch

class AnalysisViewModel(application: Application) : ViewModel() {

    private val dataDao: TransactionDao = TransactionRoomDatabase.getDatabase(application).transactionDao()
    val allData: LiveData<List<Transaction>> = dataDao.getAllTransactionASC()

}