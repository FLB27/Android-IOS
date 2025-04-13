package fr.isen.fallabrinom.isensmartcompanion.AI

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import fr.isen.fallabrinom.isensmartcompanion.databaseRoom.History
import fr.isen.fallabrinom.isensmartcompanion.databaseRoom.HistoryViewModel
import fr.isen.fallabrinom.isensmartcompanion.event.EventViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Gemini : ViewModel() {
    private val apiKey = "AIzaSyBB2tJIAvjKVbHNzV283i-8bx2sDX8cyHE" // Clé API générée sur Google AI Studio
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    var chatMessages = mutableStateListOf<Pair<String, String>>() // Stocke les messages

    fun sendMessage(userMessage: String,historyViewModel: HistoryViewModel) {
        chatMessages.add(userMessage to "En attente...") //on met la phrase en attente

        viewModelScope.launch(Dispatchers.IO) { //coroutine dans le viewModelScope, gérer les tâches asynchrones de manière sécurisée (elles seront annulées automatiquement lorsque le ViewModel sera détruit
            try {
                val response = generativeModel.generateContent(userMessage) //envoie un message utilisateur (userMessage) à l’API Gemini pour obtenir une réponse générée
                val textResponse = response.text ?: "Réponse vide" //affiche la réponse s'il y en a une

                val newHistory = History( //on crée une instance pour mettre dans notre collection
                    user = userMessage,
                    bot = textResponse,
                    date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()) // Fonction pour récupérer la date actuelle
                )
                historyViewModel.addHistory(newHistory) //sauvegarde de l'instance dans la bdd

                chatMessages[chatMessages.lastIndex] = userMessage to textResponse //on ajoute la valeur reçue dans la réponse à userMessage et on mstocke ce message en dernier index dans la liste (gestion d'historique)
            } catch (e: Exception) {
                chatMessages[chatMessages.lastIndex] = userMessage to "Erreur : ${e.message}"
            }
        }
    }

    fun analysePlanning(prompt: String, onResult: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(prompt)
                onResult(response.text ?: "Aucune réponse")
            } catch (e: Exception) {
                onResult("Erreur lors de l'analyse")
            }
        }
    }



}