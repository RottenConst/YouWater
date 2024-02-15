package ru.iwater.youwater.screen.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.iwater.youwater.data.Client
import ru.iwater.youwater.data.Favorite
import ru.iwater.youwater.data.NewAddress
import ru.iwater.youwater.data.NewProduct
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

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    context: Context,
    client: Client?,
    productsList: List<NewProduct>,
    addProductInBasket: (NewProduct, Int, Boolean) -> Unit,
    updateProduct: (NewProduct, Int) -> Unit,
    deleteProduct: (Int) -> Unit,
    addressList: List<NewAddress>,
    favorite: Favorite,
    onCheckedFavorite: (NewProduct, Boolean) -> Unit,
    deleteAddress: (Int) -> Unit,
    createNewAddress: (String, String, String, String, String, String, String, String, String, String, Boolean, NavHostController) -> Unit,
    deleteAccount: () -> Unit,
    getEditClientPhone: (String) -> String,
    setClientName: (String, String, String) -> Boolean,
    setClientPhone: (String, String, String) -> Boolean,
    setClientEmail: (String, String, String) -> Boolean,
    setMailing: (Boolean) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MainNavRoute.HomeScreen.path
    ) {
        addHomeScreen(
            favorite = favorite,
            addProductInBasket = addProductInBasket,
            onCheckedFavorite = onCheckedFavorite,
            navController = navController,
            navGraphBuilder = this
        )
        addAboutProductScreen(addProductInBasket = addProductInBasket, navController, this)

        addCatalogScreen( navController, this)
        addProductByCategoryScreen(
            favorite = favorite,
            addProductInBasket = addProductInBasket,
            onCheckedFavorite = onCheckedFavorite,
            navController = navController,
            navGraphBuilder = this
        )

        addBasketScreen(productsList = productsList, updateProduct = updateProduct, deleteProduct = deleteProduct, navController = navController, navGraphBuilder = this)
        addCreateOrderScreen(
            clientId = client?.id ?: 0,
            clientName = client?.name ?: "",
            clientPhone = client?.phone ?: "",
            addressList = addressList,
            navController = navController,
            navGraphBuilder = this
        )
        addCompleteOrderScreen( navController = navController, navGraphBuilder = this)

        addProfileScreen(clientName = client?.name ?: "", navController, this)
        addMyOrdersScreen( navController = navController, this)
        addUserDataScreen(clientName = client?.name ?: "", clientPhone = client?.phone ?: "", clientEmail = client?.email ?: "", deleteAccount = deleteAccount, this)
        addEditUserData(
            clientName = client?.name ?: "",
            clientPhone = client?.phone ?: "",
            clientEmail = client?.email ?: "",
            getEditClientPhone = getEditClientPhone,
            setClientName = setClientName,
            setClientPhone = setClientPhone,
            setClientEmail = setClientEmail,
            navGraphBuilder = this
        )
        addFavoriteScreen(favorite = favorite, addProductInBasket = addProductInBasket, onCheckedFavorite = onCheckedFavorite, navController = navController, this)
        addAddressesScreen(addressList = addressList, deleteAddress = deleteAddress,  navController = navController, this)
        addAddAddressScreen(createNewAddress = createNewAddress, navController = navController, navGraphBuilder = this)
        addNotificationScreen(mailingConsent = client?.mailingConsent == true, setMailing = setMailing, this)

        addAboutCompanyScreen(this)
        addContactScreen(context, this)
        addDeliveryInfoScreen(this)
        addFaqScreen(this)
    }
}

private fun addHomeScreen(
    favorite: Favorite,
    addProductInBasket: (NewProduct, Int, Boolean) -> Unit,
    onCheckedFavorite: (NewProduct, Boolean) -> Unit,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = MainNavRoute.HomeScreen.path) {
        HomeScreen(addProductInBasket = addProductInBasket, onCheckedFavorite = onCheckedFavorite, favorite = favorite, navController = navController)
    }
}

private fun addAboutProductScreen(
    addProductInBasket: (NewProduct, Int, Boolean) -> Unit,
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
            addProductInBasket = addProductInBasket,
            productId = args?.getInt(MainNavRoute.AboutProductScreen.productId)!!,
            navController = navController
        )
    }
}

private fun addCatalogScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.CatalogScreen.path
    ) {
        CatalogScreen(navController = navController)
    }
}

private fun addProductByCategoryScreen(
    favorite: Favorite,
    addProductInBasket: (NewProduct, Int, Boolean) -> Unit,
    onCheckedFavorite: (NewProduct, Boolean) -> Unit,
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

        ProductByCategory(favorite = favorite, addProductInBasket = addProductInBasket, onCheckedFavorite = onCheckedFavorite, catalogId = args?.getInt(MainNavRoute.ProductsByCategoryScreen.catalogId)!!, navController = navController)
    }
}

private fun addBasketScreen(
    productsList: List<NewProduct>,
    deleteProduct: (Int) -> Unit,
    updateProduct: (NewProduct, Int) -> Unit,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.BasketScreen.path
    ) {
        BasketScreen(productsList = productsList, updateProduct = updateProduct, deleteProduct = deleteProduct,navController = navController)
    }
}

private fun addCreateOrderScreen(
    clientId: Int,
    clientName: String,
    clientPhone: String,
    addressList: List<NewAddress>,
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
            clientId = clientId,
            clientName = clientName,
            clientPhone = clientPhone,
            addressList = addressList,
            repeatOrder = args?.getInt(MainNavRoute.CreateOrderScreen.lastOrderId)!!,
            isShowMessage = args.getBoolean(MainNavRoute.CreateOrderScreen.isShowMessage),
            navController = navController,
        )
    }
}

private fun addProfileScreen(
    clientName: String,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.ProfileMenuScreen.path
    ) {
        ProfileScreen(clientName = clientName, navController = navController)
    }
}

private fun addMyOrdersScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.MyOrderScreen.path
    ) {
        MyOrdersScreen(navController = navController)
    }
}

private fun addEditUserData(
    clientName: String,
    clientPhone: String,
    clientEmail: String,
    getEditClientPhone: (String) -> String,
    setClientName: (String, String, String) -> Boolean,
    setClientPhone: (String, String, String) -> Boolean,
    setClientEmail: (String, String, String) -> Boolean,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.EditUserDataScreen.path
    ) {
        EditUserDataScreen(
            clientName = clientName,
            clientPhone = clientPhone,
            clientEmail = clientEmail,
            getEditClientPhone,
            setClientName,
            setClientPhone,
            setClientEmail,
        )
    }
}

private fun addUserDataScreen(
    clientName: String,
    clientPhone: String,
    clientEmail: String,
    deleteAccount: () -> Unit,
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

        UserDataScreen(
            clientName = clientName,
            clientPhone = clientPhone,
            clientEmail = clientEmail,
            deleteAccount = deleteAccount,
            sendUserData = args?.getBoolean(MainNavRoute.UserDataScreen.sendUserData)!!
        )
    }
}

private fun addFavoriteScreen(
    favorite: Favorite,
    addProductInBasket: (NewProduct, Int, Boolean) -> Unit,
    onCheckedFavorite: (NewProduct, Boolean) -> Unit,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.FavoriteProductScreen.path
    ) {
        FavoriteScreen(addProductInBasket = addProductInBasket, favorite = favorite, onCheckedFavorite = onCheckedFavorite, navController = navController)
    }
}

private fun addAddressesScreen(
    addressList: List<NewAddress>,
    deleteAddress: (Int) -> Unit,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.AddressesScreen.path
    ) {
        AddressesScreen(deleteAddress = deleteAddress, addressList1 = addressList, navController = navController)
    }
}

private fun addAddAddressScreen(
    navController: NavHostController,
    createNewAddress: (String, String, String, String, String, String, String, String, String, String, Boolean, NavHostController) -> Unit,
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

        AddAddressScreen(createNewAddress = createNewAddress, navController = navController, isFromOrder = args?.getBoolean(MainNavRoute.AddAddressScreen.isFromOrder)!!)
    }
}

private fun addNotificationScreen(
    mailingConsent: Boolean,
    setMailing: (Boolean) -> Unit,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.NotificationScreen.path
    ) {
        NotificationScreen(mailingConsent = mailingConsent, setMailing = setMailing)
    }
}

private fun addCompleteOrderScreen(
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
    context: Context,
    navGraphBuilder: NavGraphBuilder,
) {
    navGraphBuilder.composable(
        route = MainNavRoute.ContactScreen.path
    ) {
        ContactScreen(context = context)
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