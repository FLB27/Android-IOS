package fr.isen.fallabrinom.isensmartcompanion.notifs

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext


@Composable
fun RequestNotificationPermission() { //utile uniquement pour Android 13+
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Permission accordée ✅", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permission refusée ❌", Toast.LENGTH_SHORT).show()
            }
        }
        LaunchedEffect(Unit) { //effectue la demande automatiquement
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

