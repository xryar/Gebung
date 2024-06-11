package com.example.gebung.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transaction_table ORDER BY id DESC LIMIT 4")
    fun getLastTransaction(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transaction_table ORDER BY id DESC")
    fun getAllTransaction(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transaction_table ORDER BY id ASC")
    fun getAllTransactionASC(): LiveData<List<Transaction>>

//    @Query("SELECT SUM(amount) FROM transaction_table WHERE type = :transactionType")
//    fun getTotalNominal(transactionType: String): LiveData<Int>

    @Query("SELECT SUM(amount) FROM transaction_table WHERE type = 'Expense' AND strftime('%Y-%m', Date) = :month")
    fun getTotalExpenseForMonth(month:String): LiveData<Int>

    @Query("SELECT strftime('%Y-%m', date) as month, SUM(amount) as total FROM transaction_table WHERE type = :transactionType GROUP BY month ORDER BY month ASC")
    fun getMonthlyTotals(transactionType: String): LiveData<List<MonthlyTotal>>

}