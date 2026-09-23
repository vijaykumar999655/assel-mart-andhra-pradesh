package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.EncryptedMessageEntity
import com.example.data.model.EscrowOrderEntity
import com.example.data.model.RoosterEntity
import com.example.data.model.SellerProfileEntity
import com.example.data.model.UserAccountEntity
import com.example.data.model.UserTransactionEntity

@Database(
    entities = [
        RoosterEntity::class,
        SellerProfileEntity::class,
        EncryptedMessageEntity::class,
        EscrowOrderEntity::class,
        UserAccountEntity::class,
        UserTransactionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AseelDatabase : RoomDatabase() {
    abstract fun roosterDao(): RoosterDao
    abstract fun sellerDao(): SellerDao
    abstract fun chatDao(): ChatDao
    abstract fun escrowDao(): EscrowDao
    abstract fun userAccountDao(): UserAccountDao
    abstract fun userTransactionDao(): UserTransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AseelDatabase? = null

        fun getDatabase(context: Context): AseelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AseelDatabase::class.java,
                    "aseel_mart_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
