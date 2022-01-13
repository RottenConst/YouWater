package ru.iwater.youwater.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BankCard(
    @PrimaryKey
    val numberCard: Long,
    val validateCard: String,
    val cardCVV: Int,
    var checkCard: Boolean
)
