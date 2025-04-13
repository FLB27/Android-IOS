package fr.isen.fallabrinom.isensmartcompanion.event

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fr.isen.fallabrinom.isensmartcompanion.Event
import fr.isen.fallabrinom.isensmartcompanion.API.RetrofitInstance
import fr.isen.fallabrinom.isensmartcompanion.databaseRoom.DatabaseManager
import fr.isen.fallabrinom.isensmartcompanion.databaseRoom.History
import fr.isen.fallabrinom.isensmartcompanion.notifs.NotificationManager
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Cette classe permet de stocker et mettre à jour le nombre d’événements non lus en temps réel

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseManager.getDatabase(application)
    private val dao = db.eventDao()

    val notificationManager = NotificationManager(application) //gestion de l'environnement des notifs

    private val _events = MutableLiveData<List<Event>>() // Stocke la liste des événements
    // LiveData qui observe la base de données
    val events: LiveData<List<Event>> get() = _events // Expose une version en lecture seule des données de la classe événement indirectement liés à la bdd
    //val events: LiveData<List<Event>> = dao.getAllEvent() // Expose une version en lecture seule

    val allevents: LiveData<List<Event>> = dao.getAllEvent()



    private val _eventCount = MutableLiveData<Int>(_events.value?.size ?: 0) // Initialise le compteur d'événements à la taille de la liste
    //events.value renvoie la liste d’événements stockée dans _events
    // ?.size opérateur de sécurité de nullité si size = null ça renvoie pas d'erreur car renvoie null
    // ?: 0 si elle est null donc vide on renvoie 0 pour forcer
    val eventCount: LiveData<Int> get() = _eventCount

    val acceptedEvents: LiveData<List<Event>> = dao.getAcceptedEvents()


   init {
        fetchEventsFromApiIfNeeded() // Charge les événements à l'initialisation de la classe automatiquement et uniquement
    }

    fun fetchEventsFromApiIfNeeded() { //On vérifie si la bdd est déjà remplie
        viewModelScope.launch {
            //val currentEvents = dao.getEventCount() // Récupère le nombre d'events dans la bdd
            val currentEvents = dao.getUnAcceptedEventCount()
            //plus pratique de compter que récupérer une liste null car une variable LiveData est asynchrone donc elle peut ne pas avoir eu le temps de se charger et donc perturbe le process

            if (currentEvents == 0) { // Vérifie si la bdd est vide
                fetchEventsFromApi() // Si vide, appelle l'API
            }else {
                _events.postValue(dao.getUnAcceptedEvents()) //on attribue la liste
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
                            _events.value = response.body() ?: emptyList() //la variable prend la liste aussi
                            updateEventCount(_events.value?.size ?: 0) //Mise à jour du nombre de notifs
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                println("Erreur API : ${t.message}")
            }
        })
    }

    fun removeEvent(eventId: String, event: Event, sup: Boolean) { //removeEvent(eventId) filtre la liste et met à jour _events, ce qui déclenche un re-render de l’UI

        if (sup == true) {
            viewModelScope.launch {
                dao.deleteEvent(event)
            }
        }
        _events.postValue(_events.value?.filter { it.id != eventId }) //on filtre la liste pour que l'événement ajouté mais pas rejeté soit supprimé de l'affichage maus pas de la bdd
         //postvalue = mettre à jour sa valeur depuis un thread en arrière-plan
    }

    fun updateEventCount(count: Int) {
        _eventCount.value = count
    }


    fun addEvent(event: List<Event>) {
        viewModelScope.launch {
            dao.insertAllEvent(event)
        }
    }

    fun addOneEvent(event: Event){
        viewModelScope.launch {
            dao.insertOneEvent(event)
        }
    }

    fun updateEvent(eventId: String,value: Boolean){
        viewModelScope.launch {
                dao.updateEventAcceptance(eventId,value)
        }
    }

    fun getAll (){
        viewModelScope.launch {
            dao.getAllEvent()
        }
    }

    fun getAllAccepted (){
        viewModelScope.launch {
            dao.getAcceptedEvents()
        }
    }

}