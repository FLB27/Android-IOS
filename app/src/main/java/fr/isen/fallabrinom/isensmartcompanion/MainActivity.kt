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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fr.isen.fallabrinom.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ISENSmartCompanionTheme {
                    ChatScreen()
                    Greeting()
            }
        }
    }
}


@Composable
fun ChatScreen() {
    var message by remember { mutableStateOf("") } // Message en cours d'édition
    var messagesList by remember { mutableStateOf(listOf<String>()) } // Liste des messages envoyés

    val context = LocalContext.current // Pour afficher le Toast

    Scaffold(
        modifier = Modifier.fillMaxSize(), // Remplit tout l'écran
        bottomBar = { // Barre de saisie en bas
            BottomAppBar(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            ) {
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text("Tapez votre message...") },
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.LightGray,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )

                Button(
                    onClick = {
                        if (message.isNotBlank()) {
                            messagesList = messagesList + message // Ajoute le message à la liste
                            message = "" // Réinitialise la zone de saisie
                            Toast.makeText(context, "Message envoyé!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.padding(end = 8.dp)
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
    ) { innerPadding -> // ✅ Ajout du contenu principal (messages envoyés)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f), // Remplit tout l'espace dispo sauf la barre en bas
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messagesList) { msg -> //message écrit
                    Text(msg, fontSize = 18.sp)
                }
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
    Image(
        painter = painterResource(id = R.drawable.logo_isen),
        contentDescription = "Mon image locale",
        modifier = Modifier
            .size(150.dp)
            .padding(top = 10.dp) // Ajoute un espace en haut
            .padding(bottom = 0.dp) // Ajoute un espace en haut

    )
    Text(
        text = "Smart Companion",
        modifier = Modifier.padding(top = 0.dp) // 🔥 Pas d’espace supplémentaire
    )
    MessageList(
        "Pas de message",
    )
}

@Composable
fun MessageList(msg:String){
    Text(
    text = $msg,
    modifier = Modifier.padding(top = 0.dp) // 🔥 Pas d’espace supplémentaire
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ISENSmartCompanionTheme {
       // Greeting("Android")
    }
}