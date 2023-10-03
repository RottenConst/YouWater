package ru.iwater.youwater.data.payModule

data class Data(
    val amount: Amount,
    val confirmation: Confirmation,
    val created_at: String,
    val description: String,
    val id: String,
    val metadata: Metadata,
    val paid: Boolean,
    val payment_method: PaymentMethod,
    val recipient: Recipient,
    val refundable: Boolean,
    val status: String,
    val test: Boolean
)