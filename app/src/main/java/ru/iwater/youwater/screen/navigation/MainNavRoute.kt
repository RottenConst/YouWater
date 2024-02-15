package ru.iwater.youwater.screen.navigation

sealed class MainNavRoute(val path: String) {

    data object HomeScreen: MainNavRoute("home")

    data object AboutProductScreen: MainNavRoute("about") {
        const val productId = "productId"
    }

    data object CatalogScreen: MainNavRoute("catalog")

    data object ProductsByCategoryScreen: MainNavRoute("category") {
        const val catalogId = "categoryId"
    }

    data object BasketScreen: MainNavRoute("basket")

    data object CreateOrderScreen: MainNavRoute("createOrder") {
        const val isShowMessage = "isShowMessage"
        const val lastOrderId = "lastOrderId"
    }

    data object CardPaymentScreen: MainNavRoute("cardPayment") {
        const val orderId = "orderId"
    }

    data object CompleteOrderScreen: MainNavRoute("completeOrder") {
        const val orderId = "orderId"
        const val isPayment = "isPayment"
    }

    data object ProfileMenuScreen: MainNavRoute("profile")

    data object MyOrderScreen: MainNavRoute("myOrder")

    data object UserDataScreen: MainNavRoute("user") {
        const val sendUserData = "sendUserData"
    }

    data object EditUserDataScreen: MainNavRoute("editUser")

    data object FavoriteProductScreen: MainNavRoute("favorite")

    data object AddressesScreen: MainNavRoute("addresses")

    data object AddAddressScreen: MainNavRoute("addAddress") {
        const val isFromOrder = "isFromOrder"
    }

    data object NotificationScreen: MainNavRoute("notification")
    data object AboutCompanyScreen: MainNavRoute("companyInfo")
    data object ContactScreen: MainNavRoute("contact")
    data object DeliveryInfoScreen: MainNavRoute("delivery")
    data object FaqScreen: MainNavRoute("faq")


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