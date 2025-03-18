package fr.isen.fallabrinom.isensmartcompanion

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fr.isen.fallabrinom.isensmartcompanion.ui.theme.ISENSmartCompanionTheme


data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ISENSmartCompanionTheme {
                    API()
                    Greeting()
            }
        }
    }
}



@Composable
fun Greeting() {
    CenteredBox()
}

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

@Composable
fun API() {
    var message by remember { mutableStateOf("") } // Message en cours d'édition
    var messagesList by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) } // (Message, Est-ce une réponse ?)

    val context = LocalContext.current // Pour afficher le Toast
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                    //.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_isen),
                        contentDescription = "Logo",
                        modifier = Modifier.size(100.dp)
                    )
                    Text(
                        text = "Smart Companion",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        bottomBar = {
            BottomBar( //fonction qui crée la navbar + insertion txt + bouton
                message = message,
                onMessageChange = { message = it },
                onSendClick = {
                    if (message.isNotBlank()) {
                        messagesList = messagesList + (message to false) // Ajoute le message utilisateur
                        messagesList = messagesList + ("Réponse automatique" to true) // Ajoute une réponse
                        message = "" // Efface la zone de texte
                        Toast.makeText(context, "Message envoyé!", Toast.LENGTH_SHORT).show()
                    }
                },
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // ✅ Gère les marges pour éviter que le contenu soit sous la barre
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 LISTE DES MESSAGES ENTRE L'IMAGE ET LA BARRE DE SAISIE
            MessageList(
                messagesList = messagesList,
                modifier = Modifier
                    .weight(1f) // ✅ Permet à la liste de messages de s'étendre entre le haut et le bas
                    .fillMaxWidth()
                    //.padding(top = 10.dp, bottom = 5.dp)
            )
        }
    }
}

@Composable
fun BottomBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    navController: NavController
) {
    // Définition des onglets de navigation
    val homeTab = TabBarItem("Home", Icons.Filled.Home, Icons.Outlined.Home)
    val eventsTab = TabBarItem("Events", Icons.Filled.Notifications, Icons.Outlined.Notifications, badgeAmount = 7)
    val agendaTab = TabBarItem("Calendar", Icons.Filled.DateRange, Icons.Outlined.DateRange)
    val historyTab = TabBarItem("History", Icons.AutoMirrored.Filled.List, Icons.AutoMirrored.Outlined.List)
    val tabBarItems = listOf(homeTab, eventsTab, agendaTab, historyTab)

    Column {
        // TextField et bouton d'envoi
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

        // Barre de navigation
        TabView(tabBarItems,navController = navController)
    }
}

@Composable
fun MessageList(
    messagesList: List<Pair<String, Boolean>>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(messagesList) { (msg, isResponse) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isResponse) Arrangement.End else Arrangement.Start
            ) {
                Text(
                    text = msg,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            if (isResponse) Color.Red else Color.Gray,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(8.dp) //taille du contour du texte
                )
            }
        }
    }
}


@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(0)
    }

    NavigationBar {
        // looping over each tab to generate the views and navigation for each item
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tabBarItem.title)
                },
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title,
                        badgeAmount = tabBarItem.badgeAmount
                    )
                },
                label = {Text(tabBarItem.title)})
        }
    }
}

// This component helps to clean up the API call from our TabView above,
// but could just as easily be added inside the TabView without creating this custom component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
        Icon(
            imageVector = if (isSelected) {selectedIcon} else {unselectedIcon},
            contentDescription = title
        )
    }
}

// This component helps to clean up the API call from our TabBarIconView above,
// but could just as easily be added inside the TabBarIconView without creating this custom component
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
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

//Voir pour utiliser ça pour changer de page quand on clique sur un icône du navbar
/*NavHost(navController = navController, startDestination = homeTab.title) {
    composable(homeTab.title) {
        Text(homeTab.title)
    }
    composable(alertsTab.title) {
        Text(alertsTab.title)
    }
    composable(settingsTab.title) {
        Text(settingsTab.title)
    }
    composable(moreTab.title) {
        MoreView()
    }
}*/