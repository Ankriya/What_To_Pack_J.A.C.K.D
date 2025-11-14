package student.projects.prog7312_poe_jackd

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM", "Message received from: ${message.from}")

        message.notification?.let {
            NotificationHelper.showNotification(
                this,
                it.title ?: "New Message",
                it.body ?: "You have a notification",
                NotificationSettingsActivity.KEY_PROFILE_NOTIFICATIONS
            )
        }

        message.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: ${message.data}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
    }
}