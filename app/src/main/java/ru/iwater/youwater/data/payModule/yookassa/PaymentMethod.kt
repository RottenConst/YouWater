package ru.iwater.youwater.data.payModule.yookassa

data class PaymentMethod(
    val card: Card,
    val id: String,
    val saved: Boolean,
    val title: String,
    val type: String
)