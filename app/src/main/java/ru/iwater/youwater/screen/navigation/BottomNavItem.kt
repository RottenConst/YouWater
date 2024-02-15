package ru.iwater.youwater.screen.navigation

import ru.iwater.youwater.R

sealed class BottomNavItem(var title: String, var icon: Int, var screenRoute: String) {

    data object Home: BottomNavItem("Главная", R.drawable.ic_home_icon, MainNavRoute.HomeScreen.path)
    data object Catalog: BottomNavItem("Каталог", R.drawable.ic_catalog_icon, MainNavRoute.CatalogScreen.path)
    data object Basket: BottomNavItem("Карзина", R.drawable.ic_basket_icon, MainNavRoute.BasketScreen.path)
    data object Profile: BottomNavItem("Профиль", R.drawable.ic_profile_icon, MainNavRoute.ProfileMenuScreen.path)
    data object More: BottomNavItem("Еще", R.drawable.ic_more_icon, "more_item")
}
