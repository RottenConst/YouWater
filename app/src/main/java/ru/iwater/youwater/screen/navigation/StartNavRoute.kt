package ru.iwater.youwater.screen.navigation

sealed class StartNavRoute(val path: String) {

    object StartScreen: StartNavRoute("Start")

    object LoginScreen: StartNavRoute("login")

    object RegisterScreen: StartNavRoute("register") {
        const val phoneNumber = "phoneNumber"
    }

    object EnterPinCodeScreen: StartNavRoute("signIn") {
        const val phoneNumber = "phoneNumber"
        const val clientId = "clientId"
    }

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