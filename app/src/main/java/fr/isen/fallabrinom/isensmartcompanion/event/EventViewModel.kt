package fr.isen.fallabrinom.isensmartcompanion.event

import android.app.Application
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import fr.isen.fallabrinom.isensmartcompanion.Event
import fr.isen.fallabrinom.isensmartcompanion.API.RetrofitInstance
import fr.isen.fallabrinom.isensmartcompanion.databaseRoom.DatabaseManager
import fr.isen.fallabrinom.isensmartcompanion.notifs.NotificationManager
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Cette classe permet de stocker et mettre à jour le nombre d’événements non lus en temps réel

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseManager.getDatabase(application)
    private val eventdao = db.eventDao()

    val notificationManager = NotificationManager(application) //gestion de l'environnement des notifs

    private val _events = MutableLiveData<List<Event>>() // Stocke la liste des événements
    //val events: LiveData<List<Event>> = _events //classe qui encapsule des données et permet à plusieurs observateurs (comme des composants UI) de recevoir des mises à jour automatiquement lorsque les données changent
    // LiveData qui observe la base de données
    val events: LiveData<List<Event>> = eventdao.getAllEvent()


    private val _eventCount = MutableLiveData<Int>(_events.value?.size ?: 0) // Initialise le compteur d'événements à la taille de la liste
    //events.value renvoie la liste d’événements stockée dans _events
    // ?.size opérateur de sécurité de nullité si size = null ça renvoie pas d'erreur car renvoie null
    // ?: 0 si elle est null donc vide on renvoie 0 pour forcer
    val eventCount: LiveData<Int> get() = _eventCount

    val acceptedEvents: LiveData<List<Event>> = eventdao.getAcceptedEvents()

    /*fun fetchEvents() {
        RetrofitInstance.api.getEvents().enqueue(object : Callback<List<Event>> { //enqueue permet de faire l'action en arrière plan sans bloquer l'interface
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) { // Vérifie que la réponse est OK (200)
                    _events.value = response.body() ?: emptyList() // Met à jour les événements
                    updateEventCount(_events.value?.size ?: 0) //Mise à jour du nombre de notifs
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                println("Erreur : ${t.message}") // Affiche l'erreur en cas d'échec
            }
        })
    }*/

    init {
        fetchEventsFromApiIfNeeded() // Charge les événements à l'initialisation uniquement
    }

    private fun fetchEventsFromApiIfNeeded() { //On vérifie si la bdd est déjà remplie
        viewModelScope.launch {
            val currentEvents = eventdao.getAllEvent().value // Récupère les événements en base

            if (currentEvents.isNullOrEmpty()) { // Vérifie si la bdd est vide
                fetchEventsFromApi() // Si vide, appelle l'API
            }
        }
    }

    private fun fetchEventsFromApi() {
        RetrofitInstance.api.getEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    response.body()?.let { events ->
                        viewModelScope.launch {
                            addEvent(events) // Sauvegarde les événements en base
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                println("Erreur API : ${t.message}")
            }
        })
    }

    fun removeEvent(eventId: String, event: Event) { //removeEvent(eventId) filtre la liste et met à jour _events, ce qui déclenche un re-render de l’UI
        _events.value = _events.value?.filter { it.id != eventId } // Supprime l'événement avec l'ID donné
        viewModelScope.launch {
            eventdao.deleteEvent(event)
        }
    }

    fun updateEventCount(count: Int) {
        _eventCount.value = count
    }


    fun addEvent(event: List<Event>) {
        viewModelScope.launch {
            eventdao.insertAllEvent(event)
        }
    }

    fun updateEvent(eventId: String,value: Boolean){
        viewModelScope.launch {
                eventdao.updateEventAcceptance(eventId,value)
        }
    }

}