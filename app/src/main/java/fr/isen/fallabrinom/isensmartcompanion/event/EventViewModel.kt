package fr.isen.fallabrinom.isensmartcompanion.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fr.isen.fallabrinom.isensmartcompanion.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Cette classe permet de stocker et mettre à jour le nombre d’événements non lus en temps réel

class EventViewModel : ViewModel() {
   /* private val _events = MutableLiveData<List<Event>>(listOf(
        Event(
            id = "1",
            title = "Gala",
            description = "Bal dansant avec tout l'ISEN alors ramène toi dans la plus bestiale de tes tenues !",
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
    ))*/
    private val _events = MutableLiveData<List<Event>>() // Stocke la liste des événements
    val events: LiveData<List<Event>> = _events //classe qui encapsule des données et permet à plusieurs observateurs (comme des composants UI) de recevoir des mises à jour automatiquement lorsque les données changent
    private val _eventCount = MutableLiveData<Int>(_events.value?.size ?: 0) // Initialise le compteur d'événements à la taille de la liste
    //events.value renvoie la liste d’événements stockée dans _events
    // ?.size opérateur de sécurité de nullité si size = null ça renvoie pas d'erreur car renvoie null
    // ?: 0 si elle est null donc vide on renvoie 0 pour forcer
    val eventCount: LiveData<Int> get() = _eventCount


    fun fetchEvents() {
        RetrofitInstance.api.getEvents().enqueue(object : Callback<List<Event>> { //enqueue permet de faire l'action en arrière plan sans bloquer l'interface
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) { // Vérifie que la réponse est OK (200)
                    _events.value = response.body() // Met à jour les événements
                    updateEventCount(_events.value?.size ?: 0) //Mise à jour du nombre de notifs
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                println("Erreur : ${t.message}") // Affiche l'erreur en cas d'échec
            }
        })
    }


    fun removeEvent(eventId: String) { //removeEvent(eventId) filtre la liste et met à jour _events, ce qui déclenche un re-render de l’UI
        _events.value = _events.value?.filter { it.id != eventId } // Supprime l'événement avec l'ID donné
    }

    fun updateEventCount(count: Int) {
        _eventCount.value = count
    }




}