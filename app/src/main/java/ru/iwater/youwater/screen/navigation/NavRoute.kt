package ru.iwater.youwater.screen.navigation

sealed class NavRoute(val path: String) {

    object startScreen: NavRoute("Start")

    object loginScreen: NavRoute("login")

    object registerScreen: NavRoute("register")

    object enterPinCodeScreen: NavRoute("signIn")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}