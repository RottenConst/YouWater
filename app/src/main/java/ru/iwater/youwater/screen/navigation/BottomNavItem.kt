package ru.iwater.youwater.screen.navigation

import ru.iwater.youwater.R

sealed class BottomNavItem(var title: String, var icon: Int, var screenRoute: String) {

    object Home: BottomNavItem("Главная", R.drawable.ic_home_icon, "home")
    object Catalog: BottomNavItem("Каталог", R.drawable.ic_catalog_icon, "catalog")
    object Basket: BottomNavItem("Карзина", R.drawable.ic_basket_icon, "basket")
    object Profile: BottomNavItem("Профиль", R.drawable.ic_profile_icon, "profile")
    object More: BottomNavItem("Еще", R.drawable.ic_more_icon, "more_item")
}
