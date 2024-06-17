package com.example.gebung.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.gebung.database.Transaction
import com.example.gebung.database.TransactionDao
import com.example.gebung.database.TransactionRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class TransactionRepository(application: Application) {

    private val mTransactionDao: TransactionDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = TransactionRoomDatabase.getDatabase(application)
        mTransactionDao = db.transactionDao()
    }

    suspend fun insert(transaction: Transaction){
        mTransactionDao.insert(transaction)
    }

    fun getLastTransaction(): LiveData<List<Transaction>> = mTransactionDao.getLastTransaction()

    fun getAllTransaction(): LiveData<List<Transaction>> = mTransactionDao.getAllTransaction()

    fun getTotalExpenseForMonth(month: String): LiveData<Int>{
        return mTransactionDao.getTotalExpenseForMonth(month)
    }

    fun delete(id: Int){
        executorService.execute { mTransactionDao.delete(id) }
    }

}