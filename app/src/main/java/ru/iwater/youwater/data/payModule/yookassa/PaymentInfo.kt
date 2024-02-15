package ru.iwater.youwater.data.payModule.yookassa

import ru.iwater.youwater.data.payModule.Confirmation

data class PaymentInfo(
    val amount: Amount,
    val created_at: String,
    val description: String,
    val expires_at: String,
    val id: String,
    val metadata: Metadata,
    val paid: Boolean,
    val payment_method: PaymentMethod,
    val recipient: Recipient,
    val refundable: Boolean,
    val confirmation: Confirmation,
    val status: String,
    val test: Boolean
)