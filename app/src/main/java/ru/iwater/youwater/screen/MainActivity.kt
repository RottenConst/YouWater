package ru.iwater.youwater.screen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.coroutines.delay
import ru.iwater.youwater.R
import ru.iwater.youwater.base.BaseActivity

class MainActivity : BaseActivity() {

    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.catalogFragment,
                R.id.basketFragment,
                R.id.profileFragment,
            )
        )
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        drawerLayout = findViewById(R.id.drawer_layout)
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)

        actionBarToggle.syncState()
        navView = findViewById(R.id.nav_view)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        navController = Navigation.findNavController(this, R.id.fragment_container)

        bottom_nav_view.setupWithNavController(navController)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        bottom_nav_view.menu.getItem(4).setOnMenuItemClickListener {
            when(it.itemId){
                R.id.info_menu -> {
                    drawerLayout.openDrawer(Gravity.LEFT)
                    return@setOnMenuItemClickListener true
                }
            }
            true
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.delivery -> {
                    Toast.makeText(this, "delivery", Toast.LENGTH_LONG).show()
                    true
                }
                R.id.information -> {
                    Toast.makeText(this, "information", Toast.LENGTH_LONG).show()
                    true
                }
                R.id.send -> {
                    Toast.makeText(this, "send", Toast.LENGTH_LONG).show()
                    true
                }
                else -> false
            }
        }
    }

//    private val bottomNav = BottomNavigationView.OnNavigationItemSelectedListener { item ->
//        when(item.itemId) {
//            R.id.info_menu -> {
//                drawerLayout.openDrawer(Gravity.LEFT)
//                return@OnNavigationItemSelectedListener true
//            }
//        }
//        false
//
//    }

    companion object {
        fun start(context: Context?) {
            if (context != null) {
                ContextCompat.startActivity(
                    context, Intent(context, MainActivity::class.java),
                    null
                )
            }
        }
    }
}