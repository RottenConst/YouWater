package ru.iwater.youwater.screen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import ru.iwater.youwater.R
import ru.iwater.youwater.base.BaseActivity
import ru.iwater.youwater.databinding.MainLayoutBinding

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

    private lateinit var navController: NavController
    private lateinit var actionBarToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<MainLayoutBinding>(
            this,
            R.layout.main_layout
        )
        actionBarToggle = ActionBarDrawerToggle(this, binding.drawerLayout, 0, 0)
        binding.drawerLayout.addDrawerListener(actionBarToggle)

        actionBarToggle.syncState()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        navController = Navigation.findNavController(this, R.id.fragment_container)

        binding.bottomNavView.setupWithNavController(navController)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        binding.bottomNavView.menu.getItem(4).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.info_menu -> {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                    return@setOnMenuItemClickListener true
                }
            }
            true
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
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