package ru.iwater.youwater.data.payModule

data class Card(
    val card_type: String,
    val expiry_month: String,
    val expiry_year: String,
    val first6: String,
    val issuer_country: String,
    val issuer_name: String,
    val last4: String
)