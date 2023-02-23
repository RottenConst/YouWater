package ru.iwater.youwater.data

data class DeliverySchedule(
    val common: List<Common>,
    val exceptions: List<Exception>
)