package fr.isen.fallabrinom.isensmartcompanion.event

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import fr.isen.fallabrinom.isensmartcompanion.Event


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface EventApiService { //Retrofit fonctionne avec une interface où on définit les requêtes API qu’on veut exécuter.
    @GET("events.json") // requête GET sur l’URL
    fun getEvents(): Call<List<Event>> //Retourne une liste d’événements sous forme d’objet Call; représenter une requête HTTP et sa réponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://isen-smart-companion-default-rtdb.europe-west1.firebasedatabase.app/" // Remplace par l'URL de ton API

    val api: EventApiService by lazy {
        Retrofit.Builder() //Initialise Retrofit
            .baseUrl(BASE_URL) //définition de l'URL cible de la GET
            .addConverterFactory(GsonConverterFactory.create()) // Utilisation et mise en place de GSON
            .build()
            .create(EventApiService::class.java) // Crée une instance de notre interface API, càd que ça va utiliser la requête GET dans notre cas. génère dynamiquement un objet concret qui implémente cette interface
    }
}

@Composable
fun EventDetailsActivity(eventId: String, navController: NavController, modifier: Modifier,eventViewModel: EventViewModel) {
    val events by eventViewModel.events.observeAsState(emptyList()) //utilisée pour observer un LiveData ou un State
    // emptylist() est une valeur par défaut fournie au cas où les données n’ont pas encore été chargées ou sont nulles

    val event = events.find { it.id == eventId } // Trouve l'événement dans la liste créée dans event.kt en fonction de l'ID

    if (event != null) {
        Column(modifier = modifier.padding(16.dp)) {
            Text(text = event.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = "Date : ${event.date}", fontSize = 16.sp, color = Color.Gray)
            Text(text = event.description, fontSize = 16.sp)//,modifier = modifier.padding(top = 8.dp))
            Text(text = "Lieu : ${event.location}", fontSize = 16.sp,color=Color.Gray)//,modifier = modifier.padding(top = 8.dp)

            Button(onClick = { navController.popBackStack() }, modifier = modifier.padding(top = 2.dp)) { // popBackStack() permet de revenir à l’écran précédent
                Text("Retour")
            }
        }
    } else {
        Text("Événement non trouvé", fontSize = 18.sp, modifier = modifier.padding(8.dp))
    }
}



