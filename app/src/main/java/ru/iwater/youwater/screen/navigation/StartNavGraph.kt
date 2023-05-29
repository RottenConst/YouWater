package ru.iwater.youwater.screen.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.iwater.youwater.screen.StartActivity
import ru.iwater.youwater.screen.login.EnterPinCodeScreen
import ru.iwater.youwater.screen.login.LoginScreen
import ru.iwater.youwater.screen.login.RegisterScreen
import ru.iwater.youwater.screen.login.StartAppScreen
import ru.iwater.youwater.vm.AuthViewModel

@Composable
fun StartNavGraph(navController: NavHostController, authViewModel: AuthViewModel, startActivity: StartActivity) {
    NavHost(
        navController = navController,
        startDestination = StartNavRoute.StartScreen.path
    ) {
        addStartScreen(authViewModel, startActivity, navController, this)
        addLoginScreen(authViewModel, navController, this)
        addEnterPinCodeScreen(authViewModel, startActivity, navController, this)
        addRegisterScreen(authViewModel, navController, this)
    }
}

private fun addStartScreen(
    authViewModel: AuthViewModel,
    startActivity: StartActivity,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = StartNavRoute.StartScreen.path) {
        StartAppScreen( authViewModel = authViewModel, startActivity = startActivity, navController = navController)
    }
}

private fun addLoginScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = StartNavRoute.LoginScreen.path) {
        LoginScreen(authViewModel = authViewModel, navController = navController)
    }
}

private fun addEnterPinCodeScreen(
    authViewModel: AuthViewModel,
    startActivity: StartActivity,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = StartNavRoute.EnterPinCodeScreen.withArgsFormat(StartNavRoute.EnterPinCodeScreen.phoneNumber, StartNavRoute.EnterPinCodeScreen.clientId),
        arguments = listOf(
            navArgument(StartNavRoute.EnterPinCodeScreen.phoneNumber) {
                type = NavType.StringType
            },
            navArgument(StartNavRoute.EnterPinCodeScreen.clientId) {
                type = NavType.IntType
            }
        )
    ) { navBackStackEntry ->
        val args = navBackStackEntry.arguments

        EnterPinCodeScreen(
            phone = args?.getString(StartNavRoute.EnterPinCodeScreen.phoneNumber)!!,
            clientId = args.getInt(StartNavRoute.EnterPinCodeScreen.clientId),
            context = navController.context,
            viewModel = authViewModel,
            startActivity = startActivity
        )
    }
}

private fun addRegisterScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = StartNavRoute.RegisterScreen.withArgsFormat(StartNavRoute.RegisterScreen.phoneNumber),
        arguments = listOf(
            navArgument(StartNavRoute.RegisterScreen.phoneNumber) {
                type = NavType.StringType
            }
        )
    ) { navBackStackEntry ->
        val args = navBackStackEntry.arguments

        RegisterScreen(
            viewModel = authViewModel,
            phone = args?.getString(StartNavRoute.RegisterScreen.phoneNumber)!!, navController = navController)
    }
}