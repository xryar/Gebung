package com.example.gebung.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gebung.database.Transaction
import com.example.gebung.repository.TransactionRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class TransactionViewModel(application: Application): ViewModel() {

    private val mTransactionRepository: TransactionRepository = TransactionRepository(application)

    val totalExpense: LiveData<Int> = mTransactionRepository.getTotalExpenseNominal()

    fun insert(transaction: Transaction) = viewModelScope.launch {
        mTransactionRepository.insert(transaction)
    }

    fun getLastTransaction(): LiveData<List<Transaction>> = mTransactionRepository.getLastTransaction()

    fun getAllTransaction(): LiveData<List<Transaction>> = mTransactionRepository.getAllTransaction()


}