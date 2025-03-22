package fr.isen.fallabrinom.isensmartcompanion.nav

import fr.isen.fallabrinom.isensmartcompanion.agenda.CalendarScreen
import fr.isen.fallabrinom.isensmartcompanion.event.EventScreen
import fr.isen.fallabrinom.isensmartcompanion.history.HistoryScreen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(navController: NavHostController) { //reçoit un contrôleur de navigation pour gérer les changements d’écran
    NavHost(navController = navController, startDestination = "Home") {
        composable("Home"){}
        composable("Events") { EventScreen() }
        composable("Calendar") { CalendarScreen() }
        composable("History") { HistoryScreen() }
    }
}

