package fr.isen.fallabrinom.isensmartcompanion.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.room.util.TableInfo


@Composable
fun EventDetailsActivity(eventId: String, navController: NavController, modifier: Modifier,eventViewModel: EventViewModel) {
    val events by eventViewModel.allevents.observeAsState(emptyList()) //utilisée pour observer un LiveData ou un State
    // emptylist() est une valeur par défaut fournie au cas où les données n’ont pas encore été chargées ou sont nulles

    val event = events.find { it.id == eventId } // Trouve l'événement dans la liste créée dans event.kt en fonction de l'ID

    if (event != null) {

            Column(
                modifier = modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = event.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(text = "Date : ${event.date}", fontSize = 16.sp, color = Color.Gray)
                Text(
                    text = event.description,
                    fontSize = 16.sp
                )//,modifier = modifier.padding(top = 8.dp))
                Text(
                    text = "Lieu : ${event.location}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )//,modifier = modifier.padding(top = 8.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Button(
                        onClick = {
                            eventViewModel.removeEvent(
                                event.id,
                                event,
                                true
                            )
                        } //on le vire de la bdd
                    ) {
                        Text("Evènement fini")
                    }
                }

        }
    }else {
            Column(modifier = modifier.padding(8.dp)) {
                Text("Événement non trouvé", fontSize = 18.sp)
            }

    }
}



