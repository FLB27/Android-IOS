package fr.isen.fallabrinom.isensmartcompanion.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.fallabrinom.isensmartcompanion.databaseRoom.History
import fr.isen.fallabrinom.isensmartcompanion.databaseRoom.HistoryViewModel


@Composable
fun HistoryScreen(viewModel: HistoryViewModel, modifier: Modifier = Modifier) {
    val historyList by viewModel.history.observeAsState(emptyList())

    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(historyList) { historyItem -> // Assure-toi que c’est un `History`
            HistoryItem(historyItem, onDelete = { viewModel.deleteMessage(historyItem) }) //suppression de l'item indiqué
        }
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.deleteAll() }) {
            Text("Effacer l'historique")
        }
    }

}

@Composable
fun HistoryItem(history: History, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "User: ${history.user}", fontWeight = FontWeight.Bold)
            Text(text = "Bot: ${history.bot}")
            Text(text = "Date: ${history.date}", fontSize = 12.sp, color = Color.Gray)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Supprimer")
        }
    }
}



