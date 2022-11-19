package hu.bme.aut.android.hf.data

import androidx.room.*

@Dao
interface AccountBalanceDao {
    @Query("SELECT * FROM accountbalance")
    fun getAll(): List<AccountBalance>

    @Insert
    fun insert(accountBalances: AccountBalance): Long

    @Update
    fun update(accountBalance: AccountBalance)

    @Delete
    fun deleteBalance(accountBalance: AccountBalance)
}