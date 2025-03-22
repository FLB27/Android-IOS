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

@Composable
fun NavGraph(navController: NavHostController, paddingValues: PaddingValues ) { //reçoit un contrôleur de navigation pour gérer les changements d’écran
    NavHost(navController = navController, startDestination = "Home") {
        composable("Home"){}
        composable("Events") { EventScreen(modifier = Modifier.padding(paddingValues)) }
        composable("Calendar") { CalendarScreen(modifier = Modifier.padding(paddingValues)) }
        composable("History") { HistoryScreen(modifier = Modifier.padding(paddingValues)) }
    }
}

