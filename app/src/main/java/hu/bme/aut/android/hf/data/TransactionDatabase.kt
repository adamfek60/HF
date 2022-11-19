package hu.bme.aut.android.hf.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Transaction::class], version = 1)
@TypeConverters(value = [Transaction.Category::class])
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        fun getDatabase(applicationContext: Context): TransactionDatabase {
            return Room.databaseBuilder(
                applicationContext,
                TransactionDatabase::class.java,
                "transaction"
            ).build();
        }
    }
}