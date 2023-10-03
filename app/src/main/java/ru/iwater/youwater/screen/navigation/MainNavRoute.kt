package ru.iwater.youwater.screen.navigation

sealed class MainNavRoute(val path: String) {

    object HomeScreen: MainNavRoute("home")

    object AboutProductScreen: MainNavRoute("about") {
        const val productId = "productId"
    }

    object CatalogScreen: MainNavRoute("catalog")

    object ProductsByCategoryScreen: MainNavRoute("category") {
        const val catalogId = "categoryId"
    }

    object BasketScreen: MainNavRoute("basket")

    object CreateOrderScreen: MainNavRoute("createOrder") {
        const val isShowMessage = "isShowMessage"
        const val lastOrderId = "lastOrderId"
    }

    object CardPaymentScreen: MainNavRoute("cardPayment") {
        const val orderId = "orderId"
    }

    object CompleteOrderScreen: MainNavRoute("completeOrder") {
        const val orderId = "orderId"
        const val isPayment = "isPayment"
    }

    object ProfileMenuScreen: MainNavRoute("profile")

    object MyOrderScreen: MainNavRoute("myOrder")

    object UserDataScreen: MainNavRoute("user") {
        const val sendUserData = "sendUserData"
    }

    object EditUserDataScreen: MainNavRoute("editUser")

    object FavoriteProductScreen: MainNavRoute("favorite")

    object AddressesScreen: MainNavRoute("addresses")

    object AddAddressScreen: MainNavRoute("addAddress") {
        const val isFromOrder = "isFromOrder"
    }

    object NotificationScreen: MainNavRoute("notification")
    object AboutCompanyScreen: MainNavRoute("companyInfo")
    object ContactScreen: MainNavRoute("contact")
    object DeliveryInfoScreen: MainNavRoute("delivery")
    object FaqScreen: MainNavRoute("faq")


    fun withArgs(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

    fun withArgsFormat(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/{$arg}")
            }
        }
    }
}