package ru.iwater.youwater.screen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.RemoteMessage
import com.pusher.pushnotifications.*
import com.pusher.pushnotifications.auth.AuthData
import com.pusher.pushnotifications.auth.AuthDataGetter
import com.pusher.pushnotifications.auth.BeamsTokenProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.iwater.youwater.R
import ru.iwater.youwater.base.App
import ru.iwater.youwater.base.BaseActivity
import ru.iwater.youwater.bd.YouWaterDB
import ru.iwater.youwater.repository.AuthorisationRepository
import ru.iwater.youwater.screen.navigation.MainScreen
import ru.iwater.youwater.theme.YourWaterTheme
import ru.iwater.youwater.vm.WatterViewModel
import timber.log.Timber
import javax.inject.Inject


class MainActivity : BaseActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val screenComponent = App().buildScreenComponent()
    private lateinit var authRepository: AuthorisationRepository
    private val viewModel: WatterViewModel by viewModels { factory }


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
        authRepository = AuthorisationRepository(screenComponent.clientStorage())
        setContent {
            YourWaterTheme {
                MainScreen(watterViewModel = viewModel, mainActivity = this)
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
                            val intent = Intent(this@MainActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            val pendingIntent = PendingIntent.getActivity(baseContext, 0 /* Request code */, intent,
                                PendingIntent.FLAG_IMMUTABLE)

                            val channelId = getString(R.string.default_notification_channel_id)
                            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                            val notificationBuilder = NotificationCompat.Builder(baseContext, channelId)
                                .setSmallIcon(R.drawable.ic_your_water_logo)
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

                            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
                            }
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
                        R.string.general_yes
                    ) { _, _ ->
                        val intent = Intent(applicationContext, StartActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        CoroutineScope(Dispatchers.Default).launch {
                            YouWaterDB.getYouWaterDB(applicationContext)?.clearAllTables()
                        }
                        PushNotifications.clearAllState()
                        PushNotifications.clearDeviceInterests()
                        authRepository.deleteClient()
                        startActivity(intent)
                    }
                    .setNegativeButton(R.string.general_no) { dialog, _ ->
                        dialog.cancel()
                    }.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
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