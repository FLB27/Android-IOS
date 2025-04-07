package fr.isen.fallabrinom.isensmartcompanion.nav

//import androidx.compose.runtime.LaunchedEffect


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.isen.fallabrinom.isensmartcompanion.agenda.AgendaScreen
import fr.isen.fallabrinom.isensmartcompanion.databaseRoom.HistoryViewModel
import fr.isen.fallabrinom.isensmartcompanion.event.EventDetailsActivity
import fr.isen.fallabrinom.isensmartcompanion.event.EventScreen
import fr.isen.fallabrinom.isensmartcompanion.event.EventViewModel
import fr.isen.fallabrinom.isensmartcompanion.history.HistoryScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController, paddingValues: PaddingValues , eventViewModel: EventViewModel, historyViewModel: HistoryViewModel) { //reçoit un contrôleur de navigation pour gérer les changements d’écran

    val events by eventViewModel.events.observeAsState(emptyList()) // Observe les données
    eventViewModel.updateEventCount(events.size)

    NavHost(navController = navController, startDestination = "Home") {
        composable("Home"){}
        composable("Events") {
            EventScreen(modifier = Modifier.padding(paddingValues),navController,eventViewModel,events) }
        composable("Calendar") { AgendaScreen(modifier = Modifier.padding(paddingValues).fillMaxSize(),eventViewModel, navController) }
        composable("History") { HistoryScreen(historyViewModel,modifier = Modifier.padding(paddingValues)) }
        composable("Event/{eventId}") { backStackEntry -> //chemin d’accès (route) pour cette activité + backStackEntry contient les informations de la navigation actuelle
            val eventId = backStackEntry.arguments?.getString("eventId") ?: "" //récupérer les arguments passés dans l’URL + transformer en string l'ID
            EventDetailsActivity(eventId, navController,modifier = Modifier.padding(paddingValues),eventViewModel)
        }
    }
}

