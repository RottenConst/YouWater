package ru.iwater.youwater.utils

import com.google.firebase.messaging.RemoteMessage
import com.pusher.pushnotifications.fcm.MessagingService
import timber.log.Timber

class NotificationsMessagingService: MessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.i("Got a remote message from: ${remoteMessage.from}")
    }
}