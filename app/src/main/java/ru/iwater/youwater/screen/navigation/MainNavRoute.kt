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

    object CreateOrderScreen: MainNavRoute("createOrder")

    object CardPaymentScreen: MainNavRoute("cardPayment")

    object CompleetOrderScreen: MainNavRoute("completeOrder")

    object ProfileMenuScreen: MainNavRoute("profile")

    object MyOrderScreen: MainNavRoute("myOrder")

    object UserDataScreen: MainNavRoute("user")

    object EditUserDataSceen: MainNavRoute("editUser")

    object FavoriteProductScreen: MainNavRoute("favorite")

    object AddressesScreen: MainNavRoute("addresses")

    object AddAddressScreen: MainNavRoute("addAddress")

    object NotificationScreen: MainNavRoute("notification")


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