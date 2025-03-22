package fr.isen.fallabrinom.isensmartcompanion.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun HistoryScreen(modifier: Modifier = Modifier) { //le paramètre modifier permet de faire passer le padding de référence de la page
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("History Screen")
    }
}