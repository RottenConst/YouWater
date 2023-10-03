package ru.iwater.youwater.data.payModule

data class PaymentMethod(
    val card: Card,
    val id: String,
    val saved: Boolean,
    val title: String,
    val type: String
)