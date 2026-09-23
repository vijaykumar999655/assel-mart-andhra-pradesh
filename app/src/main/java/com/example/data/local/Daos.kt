package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.EncryptedMessageEntity
import com.example.data.model.EscrowOrderEntity
import com.example.data.model.RoosterEntity
import com.example.data.model.SellerProfileEntity
import com.example.data.model.UserAccountEntity
import com.example.data.model.UserTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoosterDao {
    @Query("SELECT * FROM roosters ORDER BY isFeatured DESC, createdTimestamp DESC")
    fun getAllRoosters(): Flow<List<RoosterEntity>>

    @Query("SELECT * FROM roosters WHERE id = :id LIMIT 1")
    fun getRoosterById(id: String): Flow<RoosterEntity?>

    @Query("SELECT * FROM roosters WHERE sellerId = :sellerId ORDER BY createdTimestamp DESC")
    fun getRoostersBySeller(sellerId: String): Flow<List<RoosterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooster(rooster: RoosterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(roosters: List<RoosterEntity>)

    @Update
    suspend fun updateRooster(rooster: RoosterEntity)

    @Query("DELETE FROM roosters WHERE id = :id")
    suspend fun deleteRoosterById(id: String)
}

@Dao
interface SellerDao {
    @Query("SELECT * FROM sellers ORDER BY rating DESC")
    fun getAllSellers(): Flow<List<SellerProfileEntity>>

    @Query("SELECT * FROM sellers WHERE sellerId = :sellerId LIMIT 1")
    fun getSellerById(sellerId: String): Flow<SellerProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeller(seller: SellerProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sellers: List<SellerProfileEntity>)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM messages WHERE roosterId = :roosterId ORDER BY timestampMillis ASC")
    fun getMessagesForRooster(roosterId: String): Flow<List<EncryptedMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: EncryptedMessageEntity)
}

@Dao
interface EscrowDao {
    @Query("SELECT * FROM escrow_orders ORDER BY timestampMillis DESC")
    fun getAllOrders(): Flow<List<EscrowOrderEntity>>

    @Query("SELECT * FROM escrow_orders WHERE orderId = :orderId LIMIT 1")
    fun getOrderById(orderId: String): Flow<EscrowOrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: EscrowOrderEntity)

    @Query("UPDATE escrow_orders SET status = :newStatus WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, newStatus: String)
}

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts WHERE userId = :userId LIMIT 1")
    fun getUserById(userId: String): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_accounts WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts ORDER BY createdAtMillis ASC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Update
    suspend fun updateUser(user: UserAccountEntity)

    @Query("DELETE FROM user_accounts WHERE userId = :userId")
    suspend fun deleteUser(userId: String)
}

@Dao
interface UserTransactionDao {
    @Query("SELECT * FROM user_transactions WHERE userId = :userId ORDER BY dateMillis DESC")
    fun getTransactionsForUser(userId: String): Flow<List<UserTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: UserTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<UserTransactionEntity>)
}
