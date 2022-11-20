package hu.bme.aut.android.hf.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "transactions")
data class Transaction(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "income") var income: Boolean,
    @ColumnInfo(name = "expense") var expense: Boolean,
    @ColumnInfo(name = "category") var category: Category,
    @ColumnInfo(name = "amount") var amount: Int
) {
    enum class Category {
        TRANSFER, BILLS, HOSPITALITY, ENTERTAINMENT, SHOPPING, HEALTH, CASH_WITHDRAW, CASH_DEPOSIT, TRANSPORT, CLOTHING, OTHER;
        companion object {
            @JvmStatic
            @TypeConverter
            fun getByOrdinal(ordinal: Int): Category? {
                var ret: Category? = null
                for (cat in values()) {
                    if (cat.ordinal == ordinal) {
                        ret = cat
                        break
                    }
                }
                return ret
            }

            @JvmStatic
            @TypeConverter
            fun toInt(category: Category): Int {
                return category.ordinal
            }
        }
    }
}