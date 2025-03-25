package fr.isen.fallabrinom.isensmartcompanion.event

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController


@Composable
fun EventScreen(
    modifier: Modifier,
    navHostController: NavHostController,
    eventViewModel: EventViewModel,
    events: List<Event>
) {
    // Liste des événements (tu peux aussi la récupérer depuis une base de données ou une API)
    val context = LocalContext.current // Pour afficher le Toast

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
                    Toast.makeText(context, "Accepted: ${event.title}", Toast.LENGTH_SHORT).show()
                    eventViewModel.updateEventCount(eventViewModel.eventCount.value!! - 1) //on supprime une notif de non lu
                    eventViewModel.removeEvent(event.id) //on supprime l'élément cliqué de l'interface
                },
                onReject = {
                    // Logique pour rejeter l'événement
                    Toast.makeText(context, "Refused: ${event.title}", Toast.LENGTH_SHORT).show()
                    eventViewModel.updateEventCount(eventViewModel.eventCount.value!! - 1)
                    eventViewModel.removeEvent(event.id)

                },
                navHostController
            )
        }
    }

}

@Composable
fun EventBubble(event: Event, onAccept: () -> Unit, onReject: () -> Unit,navHostController: NavHostController) {
    //var expanded by remember{mutableStateOf(false)} // État pour afficher/cacher les détails
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = event.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Date: ${event.date}",
                fontSize = 12.sp,
                color = Color.Gray,
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
                    Text("Accept",fontSize = 14.sp,)
                }
                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Reject",fontSize = 14.sp,)
                }
                Button(
                    onClick = { navHostController.navigate("Event/${event.id}") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ){
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Details",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

    }
}