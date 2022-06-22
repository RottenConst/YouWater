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
import androidx.navigation.findNavController
import androidx.navigation.navOptions
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
import ru.iwater.youwater.screen.home.HomeFragmentDirections
import ru.iwater.youwater.screen.profile.UserDataFragment
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var authRepository: AuthorisationRepository
    private val screenComponent = App().buildScreenComponent()

    private val authURLString = "http://docs.iwatercrm.ru:8080/pusher/beams-auth"
    private val authToken = "mN#h8MjPw3KJ!vi"
    private val tokenProvider = BeamsTokenProvider(
        authURLString,
        object: AuthDataGetter {
            override fun getAuthData(): AuthData {
                return AuthData(
                    headers = hashMapOf("X-Key" to authToken),
                    queryParams = hashMapOf()
                )
            }
        }
    )

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

        visibilityActionBar(navController, toolbar)
        visibilityNavElements(navController, binding)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delivery -> {
                    navController.navigate(R.id.deliveryInfoFragment)
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.information -> {
                    navController.navigate(R.id.aboutCompanyFragment)
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.faq -> {
                    navController.navigate(R.id.faqFragment)
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.send -> {
                    navController.navigate(R.id.contactFragment)
                    binding.drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        PushNotifications.setUserId(
            "user-${authRepository.getAuthClient().clientId}",
            tokenProvider,
            object: BeamsCallback<Void, PusherCallbackError> {
                override fun onFailure(error: PusherCallbackError) {
                    Timber.e("Could not login to Beams: ${error.message}")
                }

                override fun onSuccess(vararg values: Void) {
                    Timber.d("Beams login success")
                    PushNotifications.setOnMessageReceivedListenerForVisibleActivity(this@MainActivity, object : PushNotificationReceivedListener {
                        override fun onMessageReceived(remoteMessage: RemoteMessage) {
                            val messagePayload = remoteMessage.data
//                            if (messagePayload.isNotEmpty()) {
//                                Timber.d("Error")
//                            } else {
                                val intent = Intent(this@MainActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                val pendingIntent = PendingIntent.getActivity(baseContext, 0 /* Request code */, intent,
                                    PendingIntent.FLAG_ONE_SHOT)

                                val channelId = getString(R.string.default_notification_channel_id)
                                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                                val notificationBuilder = NotificationCompat.Builder(baseContext, channelId)
                                    .setSmallIcon(R.drawable.ic_youwater_logo)
                                    .setContentTitle(remoteMessage.notification?.title)
                                    .setContentText(remoteMessage.notification?.body)
                                    .setAutoCancel(true)
                                    .setWhen(System.currentTimeMillis())
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent)


                                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                // Since android Oreo notification channel is needed.
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    val channel = NotificationChannel(channelId,
                                        "hello",
                                        NotificationManager.IMPORTANCE_DEFAULT)
                                    channel.enableVibration(true)
                                    notificationManager.createNotificationChannel(channel)
                                }

                                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
//                            }
                        }
                    })
                }
            }
        )
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
                        PushNotifications.clearAllState()
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

    private fun visibilityActionBar(navController: NavController, toolbar: Toolbar) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.completeOrderFragment,
                R.id.cardPaymentFragment -> toolbar.visibility = View.GONE
                else -> toolbar.visibility = View.VISIBLE
            }
        }
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
                R.id.bankCardFragment,
                R.id.aboutCompanyFragment,
                R.id.contactFragment,
                R.id.deliveryInfoFragment,
                R.id.faqFragment,
                R.id.favoriteFragment,
                R.id.cardPaymentFragment,
                R.id.completeOrderFragment-> binding.bottomNavView.visibility = View.GONE
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