package ru.iwater.youwater.utils

enum class StatusData {
    LOAD,
    DONE
}

enum class StatusPinCode { ERROR, EDIT_PIN, DONE, NET_ERROR }

enum class StatusSession { CHECKED, TRY, FALSE, ERROR }

enum class StatusPayment { LOAD, PANDING, ERROR, DONE }

data class Status (
    val status: Boolean
)