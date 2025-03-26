package fr.isen.fallabrinom.isensmartcompanion.notifs

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.core.content.edit
import android.app.NotificationManager


class NotificationManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("notifications", Context.MODE_PRIVATE) //variable qui va stocker les préférences de notifs pour chaque événement

    fun isNotificationEnabled(eventId: String): Boolean {
        return sharedPreferences.getBoolean(eventId, false)  //on récupère un Boolean associé à notre ID
    }
    fun toggleNotification(eventId: String) {
        val currentState = isNotificationEnabled(eventId) //le Boolean lié à l'ID nous est retourné
        sharedPreferences.edit() { putBoolean(eventId, !currentState) } //on pousse le Boolean en True afin d'activer la notif pour cet eventID
    }

}

fun createNotificationChannel(context: Context) { //création du channel d'envoi des notifs

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Event Notifications"
        val descriptionText = "Notifications pour rappeler les événements"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("event_channel", name, importance).apply {
            description = descriptionText
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}