package com.example.gebung.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.gebung.database.Transaction
import com.example.gebung.database.TransactionDao
import com.example.gebung.database.TransactionRoomDatabase
import java.util.Calendar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TransactionRepository(application: Application) {

    private val mTransactionDao: TransactionDao

    init {
        val db = TransactionRoomDatabase.getDatabase(application)
        mTransactionDao = db.transactionDao()
    }

    suspend fun insert(transaction: Transaction){
        mTransactionDao.insert(transaction)
    }

    fun getLastTransaction(): LiveData<List<Transaction>> = mTransactionDao.getLastTransaction()

    fun getAllTransaction(): LiveData<List<Transaction>> = mTransactionDao.getAllTransaction()

    fun getTotalExpenseNominal(): LiveData<Int>{
        return mTransactionDao.getTotalNominal("Expense")
    }

}