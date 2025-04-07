package fr.isen.fallabrinom.isensmartcompanion

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.isen.fallabrinom.isensmartcompanion.AI.Gemini
import fr.isen.fallabrinom.isensmartcompanion.databaseRoom.HistoryViewModel
import fr.isen.fallabrinom.isensmartcompanion.event.EventViewModel
import fr.isen.fallabrinom.isensmartcompanion.nav.NavGraph
import fr.isen.fallabrinom.isensmartcompanion.nav.TabView
import fr.isen.fallabrinom.isensmartcompanion.notifs.NotificationManager
import fr.isen.fallabrinom.isensmartcompanion.notifs.RequestNotificationPermission
import fr.isen.fallabrinom.isensmartcompanion.notifs.createNotificationChannel
import fr.isen.fallabrinom.isensmartcompanion.ui.theme.ISENSmartCompanionTheme


data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this) //génère le channel pour envoi des notifs pour cette appli
        enableEdgeToEdge()
        setContent {
            // Demande la permission des notifs dès l'ouverture de l'écran
            RequestNotificationPermission()
            ISENSmartCompanionTheme {
                    API()
                    Greeting()
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Greeting() {
    CenteredBox()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CenteredBox() {
    Box(
        modifier = Modifier.fillMaxSize(), // Prend tout l'écran
        contentAlignment = Alignment.Center // Centre le contenu
    ) { Column(//sans verticalArrangement modifié on obtient l'image centrée en haut et  au milieu
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally // Centre l'image horizontalement
        ) {
            API()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun API() {
    var userMessage by remember { mutableStateOf("") } // Message en cours d'édition
    //var messagesList by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) } // (Message, Est-ce une réponse ?)

    val geminiViewModel: Gemini = viewModel() //ajout de Gemini pour répondre aux messages
    val chatMessages = geminiViewModel.chatMessages

    val context = LocalContext.current // Pour afficher le Toast
    val navController = rememberNavController()

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route //suit l'onglet cliqué

    val eventViewModel: EventViewModel = viewModel() // Initialisation automatique du ViewModel
    val eventCount by eventViewModel.eventCount.observeAsState(0) // Observe le nombre d'événements

    val historyViewModel: HistoryViewModel = viewModel()


    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)){
                    Image(
                        painter = painterResource(id = R.drawable.logo_isen),
                        contentDescription = "Logo",
                        modifier = Modifier.size(130.dp)
                    )
                    Text(
                        modifier = Modifier.padding(0.dp), // Supprime toute marge interne
                        text = "Smart Companion",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold ,
                    )
                }
            }
        },
        bottomBar = {
            BottomBar( //fonction qui crée la navbar + insertion txt + bouton
                message = userMessage,
                onMessageChange = { userMessage = it },
                onSendClick = {
                    if (userMessage.isNotBlank()) {
                        geminiViewModel.sendMessage(userMessage,historyViewModel)
                        userMessage = "" // Efface la zone de texte
                        Toast.makeText(context, "Message envoyé!", Toast.LENGTH_SHORT).show()
                    }
                },
                navController, //on donne le navcontroller
                eventViewModel,
                eventCount
            )
        }
    ) {paddingValues  ->

        NavGraph(navController,paddingValues,eventViewModel,historyViewModel) //gestion des onglets

        if (currentRoute == "Home") { //permet de conditionner l'affichage des échanges de message

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), // ✅ Gère les marges pour éviter que le contenu soit sous la barre
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 🔹 LISTE DES MESSAGES ENTRE L'IMAGE ET LA BARRE DE SAISIE
                MessageList(
                    messagesList = chatMessages,
                    modifier = Modifier
                        .weight(1f) // ✅ Permet à la liste de messages de s'étendre entre le haut et le bas
                        .fillMaxWidth(),
                    historyViewModel
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    navHostController: NavHostController,
    eventViewModel: EventViewModel,
    eventCount: Int,
) {
    // Définition des onglets de navigation


    val homeTab = TabBarItem("Home", Icons.Filled.Home, Icons.Outlined.Home)
    val eventsTab = TabBarItem("Events", Icons.Filled.Notifications, Icons.Outlined.Notifications, badgeAmount = eventCount)
    val agendaTab = TabBarItem("Calendar", Icons.Filled.DateRange, Icons.Outlined.DateRange)
    val historyTab = TabBarItem("History", Icons.AutoMirrored.Filled.List, Icons.AutoMirrored.Outlined.List)
    val tabBarItems = listOf(homeTab, eventsTab, agendaTab, historyTab)

    // Obtenir l'onglet actuel sélectionné
    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route //suit l'onglet cliqué

    Column {
        // TextField et bouton d'envoi
        if (currentRoute == "Home") { //permet de conditionner l'affichage de la zone d'insertion
            BottomAppBar(
                containerColor = Color.Transparent, //couleur carré autour du champ + bouton
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            ) {
                TextField(
                    value = message,
                    onValueChange = onMessageChange,
                    placeholder = { Text("Tapez votre message...") },
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.LightGray,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                Button(
                    onClick = onSendClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .shadow(4.dp, shape = RoundedCornerShape(20.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Envoyer",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Barre de navigation
        TabView(tabBarItems,navController = navHostController,eventViewModel)

    }
}


@Composable
fun MessageList(
    messagesList: SnapshotStateList<Pair<String, String>>,
    modifier: Modifier = Modifier,
    historyViewModel: HistoryViewModel
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(messagesList) { (user, bot) ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                //horizontalArrangement = if (isResponse) Arrangement.End else Arrangement.Start
            ) {
                Text(
                    text = "👤 $user",
                    fontSize = 14.sp,
                    color = Color.White,

                    modifier = Modifier
                        .background(
                            Color.Red,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(8.dp) //taille du contour du texte
                        .align(Alignment.Start)
                )
                Text(
                    text = " \uD83E\uDD16 $bot", //logo du robot
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            Color.Gray,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .align(Alignment.End)
                        .padding(8.dp) //taille du contour du texte
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ISENSmartCompanionTheme {
       // Greeting("Android")
    }
}




