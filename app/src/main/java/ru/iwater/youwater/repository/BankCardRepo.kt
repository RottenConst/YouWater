package ru.iwater.youwater.repository

import ru.iwater.youwater.bd.BankCardDao
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.data.BankCard
import javax.inject.Inject

class BankCardRepo @Inject constructor(
    youWaterDB: YouWaterDB
) {
    private val bankCardDao: BankCardDao = youWaterDB.bankCard()

    suspend fun getBankCardList(): List<BankCard> {
        return bankCardDao.getMyBankCard() ?: emptyList()
    }

    suspend fun saveBankCard(bankCard: BankCard) {
        bankCardDao.saveCard(bankCard)
    }

    suspend fun deleteBankCard(bankCard: BankCard) {
        bankCardDao.deleteCard(bankCard)
    }

    suspend fun updateBankCard(bankCard: BankCard) {
        bankCardDao.updateBankCard(bankCard)
    }
}