package ru.iwater.youwater.data.payModule.yookassa

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
    val status: String,
    val test: Boolean
)