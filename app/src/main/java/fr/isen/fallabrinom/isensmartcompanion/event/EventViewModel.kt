package fr.isen.fallabrinom.isensmartcompanion.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

//Cette classe permet de stocker et mettre à jour le nombre d’événements non lus en temps réel

class EventViewModel : ViewModel() {
    private val _eventCount = MutableLiveData(3) // Nombre d'événements non lus

    val eventCount: LiveData<Int> get() = _eventCount

    fun updateEventCount(count: Int) {
        _eventCount.value = count
    }
}