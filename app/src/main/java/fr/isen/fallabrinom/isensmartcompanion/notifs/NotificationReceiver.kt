package fr.isen.fallabrinom.isensmartcompanion.notifs

import android.Manifest
import android.annotation.SuppressLint

import fr.isen.fallabrinom.isensmartcompanion.R
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import fr.isen.fallabrinom.isensmartcompanion.MainActivity


class NotificationReceiver : BroadcastReceiver() { //écouteur d’événements système. Il attend qu’un événement particulier se produise (ici, le déclenchement d’une alarme)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) { //Lorsque l’événement survient (lorsque AlarmManager déclenche la notification)
        val title = intent.getStringExtra("title") ?: "Rappel" //Récupère le titre de la notification, sinon met “Rappel” par défaut
        val message = intent.getStringExtra("message") ?: "Ne manquez pas cet événement !"

        sendNotification(context, title, message)
    }

}

@SuppressLint("ScheduleExactAlarm")
fun scheduleNotification(context: Context, eventTime: Long, title: String, message: String) {

    val intent = Intent(context, NotificationReceiver::class.java).apply { //intention pour exécuter NotificationReceiver lorsque l’heure définie arrivera.
        putExtra("title", "Rappel d'événement: $title") //Ajoute le titre à la notif
        putExtra("message", message)
    }

    val pendingIntent = PendingIntent.getBroadcast( //permet à AlarmManager de déclencher NotificationReceiver plus tard. Exécuter un BroadcastReceiver EN TEMPS VOULU
        context,
        1001,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE //Si un PendingIntent avec cet ID existe déjà, il est mis à jour + raisons sécu
    )

    val alarmManager = context.getSystemService(AlarmManager::class.java)
    alarmManager.set(AlarmManager.RTC_WAKEUP, eventTime, pendingIntent)
//pendingIntent: action à exécuter lorsque l’alarme est déclenchée.
}



fun cancelNotification(context: Context, eventId: String) { //Suppression de la notif programmée précèdemment
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        eventId.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.cancel(pendingIntent)
    pendingIntent.cancel()

    // Supprimer la notification affichée
    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.cancel(eventId.hashCode())
}



@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun sendNotification(context: Context, title: String, message: String) { //gestion de l'envoi de la notif + esthétique notif

    val intent = Intent(context, MainActivity::class.java).apply { //intention pour exécuter NotificationReceiver lorsque l’heure définie arrivera.
        putExtra("title", "Rappel d'événement: $title") //Ajoute le titre à la notif
        putExtra("message", message)
    }

    // Crée un PendingIntent pour que l'Intent puisse être exécuté par le système au moment du clic
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0, // ID unique pour le PendingIntent
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // On met à jour si déjà existant
    )

    // Construire la notification
    val builder = NotificationCompat.Builder(context, "event_channel")
        .setSmallIcon(R.drawable.ic_my_notifications)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true) // La notification disparaît après un clic
        .setContentIntent(pendingIntent) // Définir le PendingIntent ici

    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.notify(1001, builder.build()) // ID unique pour chaque notif
}




