package fr.isen.fallabrinom.isensmartcompanion.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import fr.isen.fallabrinom.isensmartcompanion.agenda.CalendarScreen
import fr.isen.fallabrinom.isensmartcompanion.event.EventScreen
import fr.isen.fallabrinom.isensmartcompanion.history.HistoryScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import fr.isen.fallabrinom.isensmartcompanion.event.EventDetailsActivity
import fr.isen.fallabrinom.isensmartcompanion.event.EventViewModel


@Composable
fun NavGraph(navController: NavHostController, paddingValues: PaddingValues , eventViewModel: EventViewModel) { //reçoit un contrôleur de navigation pour gérer les changements d’écran
    NavHost(navController = navController, startDestination = "Home") {
        composable("Home"){}
        composable("Events") { EventScreen(modifier = Modifier.padding(paddingValues),navController,eventViewModel) }
        composable("Calendar") { CalendarScreen(modifier = Modifier.padding(paddingValues)) }
        composable("History") { HistoryScreen(modifier = Modifier.padding(paddingValues)) }
        composable("Event/{eventId}") { backStackEntry -> //chemin d’accès (route) pour cette activité + 	backStackEntry contient les informations de la navigation actuelle
            val eventId = backStackEntry.arguments?.getString("eventId") ?: "" //récupérer les arguments passés dans l’URL + transformer en string l'ID
            EventDetailsActivity(eventId, navController,modifier = Modifier.padding(paddingValues))
        }
    }
}

