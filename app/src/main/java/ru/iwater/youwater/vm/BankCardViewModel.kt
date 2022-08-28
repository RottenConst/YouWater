package ru.iwater.youwater.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.iwater.youwater.data.BankCard
import ru.iwater.youwater.repository.BankCardRepo
import javax.inject.Inject

class BankCardViewModel @Inject constructor(
    private val repoBankCard: BankCardRepo
): ViewModel() {

    private val _bankCardList: MutableLiveData<List<BankCard>> = MutableLiveData()
    val bankCardList: LiveData<List<BankCard>>
        get() = _bankCardList

    init {
        getAllBankCard()
    }


    fun getAllBankCard() {
        viewModelScope.launch {
            _bankCardList.value = repoBankCard.getBankCardList()
        }
    }

    fun saveBankCard(bankCard: BankCard) {
        viewModelScope.launch {
            if (bankCard.checkCard) {
                val bankCards = repoBankCard.getBankCardList()
                bankCards.forEach { card ->
                    card.checkCard = false
                    repoBankCard.updateBankCard(card)
                }
            }
            repoBankCard.saveBankCard(bankCard)
            getAllBankCard()
        }
    }

    fun deleteCard(bankCard: BankCard) {
        viewModelScope.launch {
            repoBankCard.deleteBankCard(bankCard)
            getAllBankCard()
        }
    }
}