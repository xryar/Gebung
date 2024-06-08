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

    @Query("SELECT SUM(nominal) FROM transaction_table WHERE type = :transactionType")
    fun getTotalNominal(transactionType: String): LiveData<Int>

}