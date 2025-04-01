package fr.isen.fallabrinom.isensmartcompanion.agenda

import android.icu.util.Calendar
import android.widget.CalendarView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import fr.isen.fallabrinom.isensmartcompanion.Event
import fr.isen.fallabrinom.isensmartcompanion.event.EventBubble
import fr.isen.fallabrinom.isensmartcompanion.event.EventViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment


@Composable
fun CalendarScreen(onDateSelected: (Long) -> Unit) {
    AndroidView(factory = { context ->
            CalendarView(context).apply {
                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth)
                    onDateSelected(calendar.timeInMillis)
                }
            }
    }
    )
}


@Composable
fun AgendaScreen(modifier: Modifier = Modifier,viewModel: EventViewModel) {
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) } //on observe l'évolution de la variable de date

    // Obtenir les événements acceptés via LiveData
    val events by viewModel.acceptedEvents.observeAsState(emptyList())

    // Filtrer les événements selon la date sélectionnée
    val filteredEvents = events.filter { it.date.toLongOrNull()== selectedDate }
   // toLongOrNull() essaie de convertir la chaîne it.date en Long. Si la conversion échoue (si la chaîne n’est pas un nombre valide), cela retournera null

    Box(modifier.fillMaxWidth(),contentAlignment = Alignment.Center){// Centre le contenu) {
        Column (modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally // Centre l'image horizontalement
        ){
            CalendarScreen { newDate ->
                selectedDate = newDate
            }

            LazyColumn {
                items(filteredEvents) { event ->
                    EventItem(event,modifier)
                }
            }
        }
    }
}


@Composable
fun EventItem(event: Event,modifier: Modifier) {
    // Affichage sous forme de carte (Card) pour chaque événement
    Card(
        modifier
            .padding(8.dp) // Ajoute un peu d'espace autour de chaque carte
            .fillMaxWidth(),
        //elevation = 4.dp // Donne une légère élévation à chaque carte
    ) {
        Column(modifier.padding(16.dp)) {
            // Affichage du titre de l'événement
            Text(
                text = event.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // Affichage de la date de l'événement
            Text(
                text = "Date: ${event.date}",
                //style = MaterialTheme.typography.body2
            )

            // Affichage de la description de l'événement
            Text(
                text = "Description: ${event.description}",
                //style = MaterialTheme.typography.body2
            )

            // Optionnel : Ajouter un bouton pour interagir avec l'événement
            Button(
                onClick = { /* Interaction utilisateur ici, par exemple, marquer l'événement comme terminé */ },
                modifier.padding(top = 8.dp)
            ) {
                Text("Voir les détails")
            }
        }
    }
}

