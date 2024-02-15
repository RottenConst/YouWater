package ru.iwater.youwater.screen.navigation

sealed class PaymentNavRoute (val path: String) {
    data object CompleteOrderScreen: PaymentNavRoute("CompleteOrder")

    data object CheckPaymentScreen: PaymentNavRoute("CheckPay")


    fun withArgs(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

}