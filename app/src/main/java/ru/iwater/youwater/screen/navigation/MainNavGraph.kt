package ru.iwater.youwater.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.iwater.youwater.screen.MainActivity
import ru.iwater.youwater.screen.basket.BasketScreen
import ru.iwater.youwater.screen.basket.CompleteOrderScreen
import ru.iwater.youwater.screen.basket.CreateOrderScreen
import ru.iwater.youwater.screen.catalog.CatalogScreen
import ru.iwater.youwater.screen.catalog.ProductByCategory
import ru.iwater.youwater.screen.home.AboutProductScreen
import ru.iwater.youwater.screen.home.HomeScreen
import ru.iwater.youwater.screen.more.AboutCompanyScreen
import ru.iwater.youwater.screen.more.ContactScreen
import ru.iwater.youwater.screen.more.DeliveryInfoScreen
import ru.iwater.youwater.screen.more.FaqScreen
import ru.iwater.youwater.screen.profile.AddAddressScreen
import ru.iwater.youwater.screen.profile.AddressesScreen
import ru.iwater.youwater.screen.profile.EditUserDataScreen
import ru.iwater.youwater.screen.profile.FavoriteScreen
import ru.iwater.youwater.screen.profile.MyOrdersScreen
import ru.iwater.youwater.screen.profile.NotificationScreen
import ru.iwater.youwater.screen.profile.ProfileScreen
import ru.iwater.youwater.screen.profile.UserDataScreen
import ru.iwater.youwater.vm.WatterViewModel

@Composable
fun MainNavGraph(modifier: Modifier = Modifier, navController: NavHostController, watterViewModel: WatterViewModel, mainActivity: MainActivity) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MainNavRoute.HomeScreen.path
    ) {
        addHomeScreen(watterViewModel, navController, this)
        addAboutProductScreen(watterViewModel, navController, this)

        addCatalogScreen(watterViewModel, navController, this)
        addProductByCategoryScreen(watterViewModel, navController, this)

        addBasketScreen(watterViewModel, navController, this)
        addCreateOrderScreen(watterViewModel, navController, this)
        addCompleteOrderScreen(watterViewModel = watterViewModel, navController = navController, navGraphBuilder = this)

        addProfileScreen(watterViewModel, navController, this)
        addMyOrdersScreen(watterViewModel = watterViewModel, navController = navController, this)
        addUserDataScreen(watterViewModel = watterViewModel, this)
        addEditUserData(watterViewModel = watterViewModel, this)
        addFavoriteScreen(watterViewModel = watterViewModel, navController = navController, this)
        addAddressesScreen(watterViewModel = watterViewModel, navController = navController, this)
        addAddAddressScreen(watterViewModel = watterViewModel, navController = navController, this)
        addNotificationScreen(watterViewModel = watterViewModel, this)

        addAboutCompanyScreen(this)
        addContactScreen(mainActivity, this)
        addDeliveryInfoScreen(this)
        addFaqScreen(this)
    }
}

private fun addHomeScreen(
    watterViewModel: WatterViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = MainNavRoute.HomeScreen.path) {
        HomeScreen(navController = navController, watterViewModel = watterViewModel)
    }
}

private fun addAboutProductScreen(
    watterViewModel: WatterViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.AboutProductScreen.withArgsFormat(MainNavRoute.AboutProductScreen.productId),
        arguments = listOf(
            navArgument(MainNavRoute.AboutProductScreen.productId) {
                type = NavType.IntType
            }
        )
    ) { navBackStackEntry ->

        val args = navBackStackEntry.arguments

        AboutProductScreen(
            watterViewModel = watterViewModel,
            productId = args?.getInt(MainNavRoute.AboutProductScreen.productId)!!,
            navController = navController
        )
    }
}

private fun addCatalogScreen(
    watterViewModel: WatterViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.CatalogScreen.path
    ) {
        CatalogScreen(watterViewModel = watterViewModel, navController = navController)
    }
}

private fun addProductByCategoryScreen(
    watterViewModel: WatterViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.ProductsByCategoryScreen.withArgsFormat(MainNavRoute.ProductsByCategoryScreen.catalogId),
        arguments = listOf(
            navArgument(MainNavRoute.ProductsByCategoryScreen.catalogId) {
                type = NavType.IntType
            }
        )
    ) { navBackStackEntry ->

        val args = navBackStackEntry.arguments

        ProductByCategory(catalogId = args?.getInt(MainNavRoute.ProductsByCategoryScreen.catalogId)!!, watterViewModel = watterViewModel, navController = navController)
    }
}

private fun addBasketScreen(
    watterViewModel: WatterViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.BasketScreen.path
    ) {
        BasketScreen(watterViewModel = watterViewModel, navController = navController)
    }
}

private fun addCreateOrderScreen(
    watterViewModel: WatterViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.CreateOrderScreen.withArgsFormat(MainNavRoute.CreateOrderScreen.isShowMessage, MainNavRoute.CreateOrderScreen.lastOrderId),
        arguments = listOf(
            navArgument(MainNavRoute.CreateOrderScreen.isShowMessage) {
                type = NavType.BoolType
            },
            navArgument(MainNavRoute.CreateOrderScreen.lastOrderId) {
                type = NavType.IntType
            }
        )
    ) { navBackStackEntry ->

        val args = navBackStackEntry.arguments

        CreateOrderScreen(
            watterViewModel = watterViewModel,
            repeatOrder = args?.getInt(MainNavRoute.CreateOrderScreen.lastOrderId)!!,
            isShowMessage = args.getBoolean(MainNavRoute.CreateOrderScreen.isShowMessage),
            navController = navController,
        )
    }
}

private fun addProfileScreen(
    watterViewModel: WatterViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.ProfileMenuScreen.path
    ) {
        ProfileScreen(watterViewModel, navController = navController)
    }
}

private fun addMyOrdersScreen(
    watterViewModel: WatterViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.MyOrderScreen.path
    ) {
        MyOrdersScreen(navController = navController, watterViewModel = watterViewModel)
    }
}

private fun addEditUserData(
    watterViewModel: WatterViewModel,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.EditUserDataScreen.path
    ) {
        EditUserDataScreen(
            watterViewModel = watterViewModel,
        )
    }
}

private fun addUserDataScreen(
    watterViewModel: WatterViewModel,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.UserDataScreen.withArgsFormat(MainNavRoute.UserDataScreen.sendUserData),
        arguments = listOf(
            navArgument(MainNavRoute.UserDataScreen.sendUserData) {
                type = NavType.BoolType
            }
        )
    ) { navBackStackEntry ->

        val args = navBackStackEntry.arguments

        UserDataScreen(sendUserData = args?.getBoolean(MainNavRoute.UserDataScreen.sendUserData)!!, watterViewModel = watterViewModel)
    }
}

private fun addFavoriteScreen(
    watterViewModel: WatterViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.FavoriteProductScreen.path
    ) {
        FavoriteScreen(navController = navController, watterViewModel = watterViewModel)
    }
}

private fun addAddressesScreen(
    watterViewModel: WatterViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.AddressesScreen.path
    ) {
        AddressesScreen(navController = navController, watterViewModel = watterViewModel)
    }
}

private fun addAddAddressScreen(
    watterViewModel: WatterViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.AddAddressScreen.withArgsFormat(MainNavRoute.AddAddressScreen.isFromOrder),
        arguments = listOf(
            navArgument(MainNavRoute.AddAddressScreen.isFromOrder) {
                type = NavType.BoolType
            }
        )
    ) {navBackStackEntry ->
        val args = navBackStackEntry.arguments

        AddAddressScreen(navController = navController, isFromOrder = args?.getBoolean(MainNavRoute.AddAddressScreen.isFromOrder)!!, watterViewModel = watterViewModel)
    }
}

private fun addNotificationScreen(
    watterViewModel: WatterViewModel,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.NotificationScreen.path
    ) {
        NotificationScreen(watterViewModel)
    }
}

private fun addCompleteOrderScreen(
    watterViewModel: WatterViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.CompleteOrderScreen.withArgsFormat(MainNavRoute.CompleteOrderScreen.orderId, MainNavRoute.CompleteOrderScreen.isPayment),
        arguments = listOf(
            navArgument(MainNavRoute.CompleteOrderScreen.orderId) {
                type = NavType.IntType
            },
            navArgument(MainNavRoute.CompleteOrderScreen.isPayment) {
                type = NavType.BoolType
            }
        )
    ) { navBackStackEntry ->
        val args = navBackStackEntry.arguments
        CompleteOrderScreen(
            watterViewModel = watterViewModel,
            orderId = args?.getInt(MainNavRoute.CompleteOrderScreen.orderId)!!,
            isPayment = args.getBoolean(MainNavRoute.CompleteOrderScreen.isPayment),
            navController = navController
        )
    }
}

private fun addAboutCompanyScreen(
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.AboutCompanyScreen.path
    ) {
        AboutCompanyScreen()
    }
}

private fun addContactScreen(
    mainActivity: MainActivity,
    navGraphBuilder: NavGraphBuilder,
) {
    navGraphBuilder.composable(
        route = MainNavRoute.ContactScreen.path
    ) {
        ContactScreen(mainActivity = mainActivity)
    }
}

private fun addDeliveryInfoScreen(
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.DeliveryInfoScreen.path
    ) {
        DeliveryInfoScreen()
    }
}

private fun addFaqScreen(
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.FaqScreen.path
    ) {
        FaqScreen()
    }
}