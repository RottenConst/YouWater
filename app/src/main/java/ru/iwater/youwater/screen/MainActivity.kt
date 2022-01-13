package ru.iwater.youwater.screen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.iwater.youwater.R
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseActivity
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.databinding.MainLayoutBinding
import ru.iwater.youwater.repository.AuthorisationRepository
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var authRepository: AuthorisationRepository
    private val screenComponent = App().buildScreenComponent()

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
        authRepository = AuthorisationRepository(screenComponent.clientStorage())
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

        visibilityNavElements(navController, binding)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.login_out_menu -> {
                AlertDialog.Builder(this)
                    .setMessage(R.string.confirmLogout)
                    .setPositiveButton(
                        R.string.yes
                    ) { _, _ ->
                        val intent = Intent(applicationContext, StartActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        CoroutineScope(Dispatchers.Default).launch {
                            YouWaterDB.getYouWaterDB(applicationContext)?.clearAllTables()
                        }
                        authRepository.deleteClient()
                        startActivity(intent)
                    }
                    .setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.cancel()
                    }.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun visibilityNavElements(navController: NavController, binding: MainLayoutBinding) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.aboutProductFragment,
                R.id.userDataFragment,
                R.id.addresessFragment,
                R.id.notificationFragment,
                R.id.addAddressFragment,
                R.id.createOrderFragment,
                R.id.myOrdersFragment,
                R.id.bankCardFragment-> binding.bottomNavView.visibility = View.GONE
                else -> binding.bottomNavView.visibility = View.VISIBLE
            }
        }
    }

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