package ru.iwater.youwater.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.iwater.youwater.screen.basket.BasketScreen
import ru.iwater.youwater.screen.catalog.CatalogScreen
import ru.iwater.youwater.screen.catalog.ProductByCategory
import ru.iwater.youwater.screen.home.AboutProductScreen
import ru.iwater.youwater.screen.home.HomeScreen
import ru.iwater.youwater.screen.profile.ProfileScreen
import ru.iwater.youwater.vm.CatalogListViewModel

@Composable
fun MainNavGraph(modifier: Modifier = Modifier,navController: NavHostController, catalogListViewModel: CatalogListViewModel) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MainNavRoute.HomeScreen.path
    ) {
        addHomeScreen(catalogListViewModel, navController, this)
        addAboutProductScreen(catalogListViewModel, navController, this)
        addCatalogScreen(catalogListViewModel, navController, this)
        addProductByCategoryScreen(navController, this)
        addBasketScreen(navController, this)
        addProfileScreen(navController, this)
    }
}

private fun addHomeScreen(
    catalogListViewModel: CatalogListViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = MainNavRoute.HomeScreen.path) {
        HomeScreen(navController = navController, catalogListViewModel = catalogListViewModel)
    }
}

private fun addAboutProductScreen(
    catalogListViewModel: CatalogListViewModel,
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
            catalogListViewModel = catalogListViewModel,
            productId = args?.getInt(MainNavRoute.AboutProductScreen.productId)!!,
            navController = navController
        )
    }
}

private fun addCatalogScreen(
    catalogListViewModel: CatalogListViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.CatalogScreen.path
    ) {
        CatalogScreen(viewModel = catalogListViewModel, navController = navController)
    }
}

private fun addProductByCategoryScreen(
//    catalogListViewModel: CatalogListViewModel,
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

        ProductByCategory(catalogId = args?.getInt(MainNavRoute.ProductsByCategoryScreen.catalogId)!!, navController = navController)
    }
}

private fun addBasketScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.BasketScreen.path
    ) {
        BasketScreen(navController = navController)
    }
}

private fun addProfileScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = MainNavRoute.ProfileMenuScreen.path
    ) {
        ProfileScreen(navController = navController)
    }
}