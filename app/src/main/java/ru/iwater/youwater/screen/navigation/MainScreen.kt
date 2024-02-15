package ru.iwater.youwater.screen.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import ru.iwater.youwater.R
import ru.iwater.youwater.data.Favorite
import ru.iwater.youwater.data.NewProduct
import ru.iwater.youwater.vm.ClientViewModel

@Composable
fun MainScreen() {
    val clientViewModel: ClientViewModel = viewModel(factory = ClientViewModel.Factory)
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope =  rememberCoroutineScope()
    var isVisibleExitDialog by remember {
        mutableStateOf(false)
    }
    val productCountInBasket by clientViewModel.productsInBasket.observeAsState()

    val context = LocalContext.current
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                DrawerBody(navController = navController) {
                    scope.launch {
                        drawerState.close()
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    navController = navController,
                    onEditUserData = {
                        clientViewModel.editUserData(navHostController = navController)
                    },
                    onExitUser = {
                        isVisibleExitDialog = !isVisibleExitDialog
                    }
                ) },
            bottomBar = { BottomNavBar(productInBasket = productCountInBasket?.size ?: 0, navController) {
                scope.launch {
                    drawerState.open()
                }
            } },
        ) { paddingValues ->
            val client by clientViewModel.client.observeAsState()
            val addressList by clientViewModel.addressList.observeAsState()
            val favorite by clientViewModel.favorite.observeAsState()

            ExitUserDialog(isVisible = isVisibleExitDialog, setVisible = {isVisibleExitDialog = !isVisibleExitDialog}) {
                clientViewModel.exitClient(client?.id ?: 0, context)
            }

            MainNavGraph(
                modifier = Modifier.padding(
                    paddingValues = paddingValues
                ),
                navController = navController,
                context = LocalContext.current,
                client = client,
                productsList = productCountInBasket ?: emptyList(),
                addProductInBasket = { product: NewProduct, count: Int, startPocket:  Boolean ->
                    clientViewModel.addProductToBasket(product, count, startPocket)
                },
                updateProduct = { product, count -> clientViewModel.updateProductCount(product, count) },
                deleteProduct = {productId: Int -> clientViewModel.deleteProductFromBasket(productId)},
                addressList = addressList ?: emptyList(),
                favorite = favorite ?: Favorite(favoritesList = emptyList()),
                onCheckedFavorite = { product: NewProduct, onFavorite: Boolean ->
                    clientViewModel.onChangeFavorite(product, onFavorite)
                },
                deleteAddress = {
                    clientViewModel.inActiveAddress(it)
                },
                createNewAddress = { region: String, city: String, street: String, house: String, block: String, entrance: String, floor: String, flat: String, contact: String, notice: String, isFromOrder: Boolean, navController: NavHostController ->
                    clientViewModel.createNewAddress(region, city, street, house, block, entrance, floor, flat, contact, notice, isFromOrder, navController)
                },
                getEditClientPhone = {
                    clientViewModel.getNumberFromPhone(it)
                },
                setClientName = { name: String, phone: String, email: String ->
                    clientViewModel.setEditClientName(name, phone, email)
                },
                setClientPhone = {phone: String, name: String, email: String ->
                    clientViewModel.setEditClientPhone(phone, name, email)
                },
                setClientEmail = {email: String, name: String, phone: String ->
                    clientViewModel.setEditClientEmail(email, name, phone)
                },
                setMailing = {
                    clientViewModel.setMailing(it)
                },
                deleteAccount = {
                    clientViewModel.deleteAccount(context = context, clientId = client?.id ?: 0)
                }
            )
        }
    }
}

@Composable
fun ExitUserDialog(isVisible: Boolean, setVisible: (Boolean) -> Unit, exit: () -> Unit) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = { setVisible(false) },
            title = {
                Text(text = stringResource(id = R.string.confirmLogout))
            },
            dismissButton = {
                TextButton(onClick = { setVisible(false) }) {
                    Text(text = stringResource(id = R.string.general_no))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    exit()
                    setVisible(false)
                }) {
                    Text(text = stringResource(id = R.string.general_yes))
                }
            }
        )
    }
}