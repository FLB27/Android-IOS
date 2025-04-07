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
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState


@Composable
fun EventDetailsActivity(eventId: String, navController: NavController, modifier: Modifier,eventViewModel: EventViewModel) {
    val events by eventViewModel.allevents.observeAsState(emptyList()) //utilisée pour observer un LiveData ou un State
    // emptylist() est une valeur par défaut fournie au cas où les données n’ont pas encore été chargées ou sont nulles

    val event = events.find { it.id == eventId } // Trouve l'événement dans la liste créée dans event.kt en fonction de l'ID

    if (event != null) {
        Column(modifier = modifier.padding(top = 4.dp)) {
            Text(text = event.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = "Date : ${event.date}", fontSize = 16.sp, color = Color.Gray)
            Text(text = event.description, fontSize = 16.sp)//,modifier = modifier.padding(top = 8.dp))
            Text(text = "Lieu : ${event.location}", fontSize = 16.sp,color=Color.Gray)//,modifier = modifier.padding(top = 8.dp)
            Row (horizontalArrangement = Arrangement.SpaceBetween){
                Button(onClick = { navController.popBackStack() }) { // popBackStack() permet de revenir à l’écran précédent
                    Text("Retour")
                }
                Button(
                        onClick = {eventViewModel.removeEvent(event.id,event,true)} //on le vire de la bdd
                ) {
                    Text("Evènement fini")
                }
            }
        }
    } else {
        Text("Événement non trouvé", fontSize = 18.sp, modifier = modifier.padding(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { navController.popBackStack() }) { // popBackStack() permet de revenir à l’écran précédent
                Text("Retour")
            }
        }
    }
}



