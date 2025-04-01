package fr.isen.fallabrinom.isensmartcompanion.databaseRoom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fr.isen.fallabrinom.isensmartcompanion.Event
import kotlinx.coroutines.launch


class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseManager.getDatabase(application)
    private val dao = db.userDao()

    private val _history = MutableLiveData<List<History>>()
    val history: LiveData<List<History>> = dao.getAll() // LiveData réactif qui voit toutes les modifs sur la bdd  en temps réel

    // Ajouter une entrée d'historique
    fun addHistory(history: History) {
        viewModelScope.launch {
            dao.insertAll(history)
        }
    }

    // Supprimer un message
    fun deleteMessage(history: History) {
        viewModelScope.launch {
            dao.delete(history) // Supprimer de la base de données
        }
    }

    // Supprimer tout l'historique
    fun deleteAll() {
        viewModelScope.launch {
            dao.deleteAll() // Supprimer tout l'historique
        }
    }



}