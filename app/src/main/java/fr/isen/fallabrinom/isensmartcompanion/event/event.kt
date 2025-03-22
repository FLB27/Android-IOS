package fr.isen.fallabrinom.isensmartcompanion.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.fallabrinom.isensmartcompanion.Event
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun EventScreen(modifier: Modifier) {
    // Liste des événements (tu peux aussi la récupérer depuis une base de données ou une API)
    val events = listOf(
        Event(
            id = "1",
            title = "Gala",
            description = "Description de l'événement 1",
            date = "2025-03-25 14:30",
            location = "ISEN Toulon",
            category = "Gala",
        ),
        Event(
            id = "2",
            title = "Grand Bonnand",
            description = "Description de l'événement 2",
            date = "2025-03-26 09:00",
            location = "Paris",
            category = "Escape Game",
        ),
        Event(
            id ="3",
            title = "Jet-Ski",
            description = "Description de l'événement 3",
            date = "2025-03-27 18:00",
            location = "Marseille bébé",
            category = "Sport"

        )
    )

    // Liste d'événements à afficher
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)

    ) {
        items(events) { event -> //prend une collection comme argument et pour chaque élément de la liste crée une ligne dans la LazyColumn
            //event permet de travailler sur chaque événement un par un et donc d'appeler la fonction suivante à chaque élément de la liste
            EventBubble( //ici on affiche les différents événements sous forme de bulle
                event = event,
                onAccept = {
                    // Logique pour accepter l'événement
                    println("Accepted: ${event.title}")
                },
                onReject = {
                    // Logique pour rejeter l'événement
                    println("Rejected: ${event.title}")
                }
            )
        }
    }
}

@Composable
fun EventBubble(event: Event, onAccept: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        //elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = event.id,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = event.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = event.description,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold

            )
            Text(
                text = "Date: ${event.date}",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold

            )
            Text(
                text = event.location,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = event.category,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button( //accepter l'activité
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("Accept")
                }
                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Reject")
                }
            }
        }
    }
}