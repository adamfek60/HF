package hu.bme.aut.android.hf.data

import androidx.room.*

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions")
    fun getAll(): List<Transaction>

    @Insert
    fun insert(transactions: Transaction): Long

    @Update
    fun update(transaction: Transaction)

    @Delete
    fun deleteTransaction(transaction: Transaction)
}