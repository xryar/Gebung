package com.example.gebung.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gebung.database.Transaction
import com.example.gebung.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application): ViewModel() {

    private val mTransactionRepository: TransactionRepository = TransactionRepository(application)

//    val totalExpense: LiveData<Int> = mTransactionRepository.getTotalExpenseNominal()

    fun insert(transaction: Transaction) = viewModelScope.launch {
        mTransactionRepository.insert(transaction)
    }

    fun getLastTransaction(): LiveData<List<Transaction>> = mTransactionRepository.getLastTransaction()

    fun getAllTransaction(): LiveData<List<Transaction>> = mTransactionRepository.getAllTransaction()

    private fun getTotalExpenseForMonth(month: String): LiveData<Int>{
        return mTransactionRepository.getTotalExpenseForMonth(month)
    }

    fun updateTotalExpense(selectedDate: String): LiveData<Int>{
        val month = selectedDate.substring(0, 7)
        return getTotalExpenseForMonth(month)
    }
}