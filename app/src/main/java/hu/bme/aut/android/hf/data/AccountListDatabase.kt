package hu.bme.aut.android.hf.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [AccountBalance::class], version = 1)
abstract class AccountListDatabase : RoomDatabase() {
    abstract fun accountBalanceDao(): AccountBalanceDao

    companion object {
        fun getDatabase(applicationContext: Context): AccountListDatabase {
            return Room.databaseBuilder(
                applicationContext,
                AccountListDatabase::class.java,
                "account-list"
            ).build()
        }
    }

}