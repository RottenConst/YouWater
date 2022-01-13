package ru.iwater.youwater.bd

import androidx.room.*
import ru.iwater.youwater.data.BankCard

@Dao
interface BankCardDao {

    @Insert
    suspend fun saveCard(bankCard: BankCard)

    @Query("SELECT * FROM BankCard")
    suspend fun getMyBankCard(): List<BankCard>?

    @Update
    suspend fun updateBankCard(bankCard: BankCard)

    @Query("SELECT * FROM BankCard WHERE numberCard IS :numberCard")
    suspend fun getBankCard(numberCard: Int): BankCard

    @Delete
    suspend fun deleteCard(bankCard: BankCard)
}