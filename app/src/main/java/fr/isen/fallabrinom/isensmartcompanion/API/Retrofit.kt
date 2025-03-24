package fr.isen.fallabrinom.isensmartcompanion.API

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