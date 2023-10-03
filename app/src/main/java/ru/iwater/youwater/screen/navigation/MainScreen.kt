package ru.iwater.youwater.screen.navigation

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.pusher.pushnotifications.PushNotifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.iwater.youwater.R
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.screen.MainActivity
import ru.iwater.youwater.screen.StartActivity
import ru.iwater.youwater.vm.WatterViewModel

@Composable
fun MainScreen(watterViewModel: WatterViewModel, mainActivity: MainActivity) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val scope =  rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(
                navController = navController,
                onEditUserData = {
                watterViewModel.editUserData(navHostController = navController)
                },
                onExitUser = {
                    AlertDialog.Builder(navController.context)
                        .setMessage(R.string.confirmLogout)
                        .setPositiveButton(
                            R.string.general_yes
                        ) { _, _ ->
                            PushNotifications.clearAllState()
                            val intent = Intent(mainActivity.applicationContext, StartActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            CoroutineScope(Dispatchers.Default).launch {
                                YouWaterDB.getYouWaterDB(mainActivity.applicationContext)?.clearAllTables()
                            }
                            watterViewModel.exitClient()
                            mainActivity.startActivity(intent)
                        }
                        .setNegativeButton(R.string.general_no) { dialog, _ ->
                            dialog.cancel()
                        }.create().show()
                }
            ) },
        bottomBar = { BottomNavBar(navController) {
            scope.launch {
                scaffoldState.drawerState.open()
            }
        } },
        drawerContent = {
            DrawerHeader()
            DrawerBody(navController = navController) {
                scope.launch {
                    scaffoldState.drawerState.close()
                }
            }
        }
    ) { paddingValues ->
        MainNavGraph(
            modifier = Modifier.padding(
                bottom = paddingValues.calculateBottomPadding()
            ),
            navController = navController,
            watterViewModel = watterViewModel,
            mainActivity = mainActivity
        )
    }
}