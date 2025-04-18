package fr.isen.fallabrinom.isensmartcompanion.agenda

//import com.kizitonwose.calendar.compose.rememberCalendarState



import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fr.isen.fallabrinom.isensmartcompanion.AI.Gemini
import fr.isen.fallabrinom.isensmartcompanion.Event
import fr.isen.fallabrinom.isensmartcompanion.event.EventViewModel
import io.wojciechosak.calendar.animation.CalendarAnimator
import io.wojciechosak.calendar.config.rememberCalendarState
import io.wojciechosak.calendar.view.CalendarView
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import kotlinx.datetime.*
import kotlinx.datetime.LocalDateTime.Companion
import java.time.format.DateTimeFormatter
import java.util.UUID


// Définir INITIAL_PAGE_INDEX comme constante
const val INITIAL_PAGE_INDEX = 12


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalCalendarView(
    startDate: LocalDate,
    pagerState: PagerState = rememberPagerState(initialPage = INITIAL_PAGE_INDEX, pageCount = { 36 }),
    modifier: Modifier = Modifier,
    newDate: Long, // Paramètre d'entrée pour la date en millisecondes
    pageSize: PageSize = PageSize.Fill,
    beyondViewportPageCount: Int = 0,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    calendarAnimator: CalendarAnimator = CalendarAnimator(startDate),
    markedDates: List<LocalDate>, //dates attachées aux events
    onDateSelected: (Long) -> Unit, // Callback pour la sélection de la date
    eventViewModel: EventViewModel,
    navHostController: NavHostController,
    event: List<Event>,
    calendarView: @Composable ((Int) -> Unit) = { monthOffset ->
        val convertedDate = Instant.fromEpochMilliseconds(newDate)
            .toLocalDateTime(TimeZone.currentSystemDefault());

        CalendarView(
            day = { dayState ->
                val isMarked = markedDates.contains(dayState.date) //si une des dates de la liste concorde avec celle du tableau
                var showEventChooser by remember { mutableStateOf(false) }
                var eventsForSelectedDate by remember { mutableStateOf<List<Event>>(emptyList()) }
                // Au début de ta fonction
                Box(
                    modifier = modifier
                        .padding(4.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)

                        .background(
                            when {
                                dayState.date == convertedDate.date -> Color.Blue.copy(alpha = 0.4f)  // Date du jour
                                isMarked -> Color.Green.copy(alpha = 0.4f)    // Date des events, couleur verte mais avec 40% d’opacité  (plus transparente)
                                else -> Color.Transparent  //Autre jour
                            }
                        )

                        .clickable {
                            val selectedDateMillis =
                                dayState.date.atStartOfDayIn(TimeZone.currentSystemDefault())
                                    .toEpochMilliseconds()// Convertir la date au format millisecondes

                            val selectedDate = Instant.fromEpochMilliseconds(selectedDateMillis)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date

                            val eventFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH)

                            val filteredEvents = event.filter {
                                try {
                                    val eventDate = java.time.LocalDate.parse(it.date, eventFormatter)
                                    eventDate == selectedDate.toJavaLocalDate()
                                } catch (e: Exception) {
                                    false
                                }
                            }

                            when (filteredEvents.size) {
                                1 -> { //si un seul event à la date on affiche que lui
                                    navHostController.navigate("Event/${filteredEvents.first().id}")
                                }
                                in 2..Int.MAX_VALUE -> { //si plus d'un event on affiche une boite de dialogue
                                    eventsForSelectedDate = filteredEvents
                                    showEventChooser = true
                                }
                            }

                        }, // Met à jour la date sélectionnée lors du clic

                    contentAlignment = Alignment.Center
                ) {
                    Text(dayState.date.dayOfMonth.toString()) //le numéro de chaque jour
                }
                if (showEventChooser) {
                    AlertDialog(
                        onDismissRequest = { showEventChooser = false },
                        title = { Text("Événements à cette date") },
                        text = {
                            Column {
                                eventsForSelectedDate.forEach { eventItem ->
                                    Text(
                                        text = eventItem.title,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navHostController.navigate("Event/${eventItem.id}")
                                                showEventChooser = false
                                            }
                                            .padding(8.dp)
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showEventChooser = false }) {
                                Text("Fermer")
                            }
                        }
                    )
                }
            },
            config = rememberCalendarState(startDate = startDate, monthOffset = monthOffset)
        )
    },
) {
    // Utilisation de mutableStateOf pour gérer l'état de la page
    var page by remember { mutableStateOf(0) } // 0 pour page précédente, 1 pour page suivante
    var monthOffset by remember { mutableStateOf(0) } // Initialement, le mois courant (0)


    Column {
        // Bar de navigation pour changer de mois
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Bouton précédent
            IconButton(onClick = { page = -1 }) {
                val newOffset = monthOffset - 1
                monthOffset = newOffset
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous month")
            }

            // Bouton suivant
            IconButton(onClick = { page = 1 }) {
                val newOffset = monthOffset + 1
                monthOffset = newOffset // Avance d'un mois sans restriction
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next month")
            }
        }

        // LaunchedEffect pour animer le changement de page
        LaunchedEffect(page) {
            if (page != 0) {
                val newPage = pagerState.currentPage + page
                pagerState.scrollToPage(newPage)  // Par exemple, faire défiler d'une page
                page = 0  // Réinitialiser après avoir effectué le changement de page
            }
        }

        // Affichage du pager horizontal pour les mois
        HorizontalPager(
            state = pagerState,
            modifier = modifier,
            pageSize = pageSize,
            beyondViewportPageCount = beyondViewportPageCount,
            contentPadding = contentPadding
        ) {
            val index = it - INITIAL_PAGE_INDEX
            calendarView(index)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End // Place à droite
        ) {
            var showDialog by remember { mutableStateOf(false) }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .background(Color(0xFF1976D2), shape = CircleShape) // Couleur bleue et fond rond
                        .size(56.dp) // Taille du fond rond
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White // Couleur de l’icône
                    )
                    if (showDialog) {
                        AddEventDialog(
                            onDismiss = { showDialog = false }, //action à réaliser si on ferme ou on clique à côté de la boîte de dialogue (ici fermeture de la boîte de dialogue)
                            onAddEvent = { event -> //on lui indique que ce paramètre onAddEvent va pouvoir utiliser la fonction addEvent sur un événement qu'il va créer
                                eventViewModel.addOneEvent(event) //ajout d'un seul event
                                showDialog = false
                            }
                        )
                    }
                }
                Text("Add", modifier = Modifier.padding(top = 4.dp))
            }
        }


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
            ) {
                var showDialog by remember { mutableStateOf(false) }

                IconButton(
                    onClick = { showDialog = true },
                    modifier = modifier
                        .size(56.dp) // Taille du fond rond
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Help,
                        contentDescription = "AI assistance",
                        tint = Color.Blue // Couleur de l’icône
                    )
                    // Affichage de l'analyse de l'IA lorsque `showAnalysis` est vrai
                    if (showDialog) {
                        analyzeWithAI(eventViewModel) {
                            showDialog = false
                        } // Appel de la fonction @Composable
                    }
                }
                Text("AI Planning Assistant", fontSize = 10.sp)//, modifier = Modifier.padding(top = 4.dp))
            }



    }


}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgendaScreen(modifier: Modifier, eventViewModel: EventViewModel,navHostController: NavHostController) {
    val currentDate: LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }

    // Événements validés
    val validevents by eventViewModel.acceptedEvents.observeAsState(emptyList())

    // Animation calendrier
    val calendarAnimator = remember { mutableStateOf(CalendarAnimator(currentDate)) }

    // Conversion des dates des événements en LocalDate
    val markedDates: List<LocalDate> = validevents.mapNotNull { event ->
        try {
            val eventFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH)
            val eventDate = java.time.LocalDate.parse(event.date, eventFormatter)
            LocalDate(eventDate.year, eventDate.monthValue, eventDate.dayOfMonth)
        } catch (e: Exception) {
            null
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // Assure que les éléments commencent tout en haut
    ) {
        HorizontalCalendarView(
            startDate = currentDate,
            newDate = selectedDate,
            calendarAnimator = calendarAnimator.value,
            markedDates = markedDates,
            onDateSelected = { newSelectedDate -> selectedDate = newSelectedDate },
            eventViewModel = eventViewModel, //on lui passe notre viexModel pour l'évènement
            navHostController = navHostController,
            event = validevents
        )

    }
}

@Composable
fun AddEventDialog(
    onDismiss: () -> Unit, //paramètre pour gérer si on quitte la pop-up
    onAddEvent: (Event) -> Unit //peut utiliser addEvent du coup
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") } // à adapter si tu veux un vrai date picker
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = { //si on ajoute l'event
            Button(onClick = {
                val newEvent = Event(
                    id = UUID.randomUUID().toString(), // ou autre selon ta logique
                    title = title,
                    description = description,
                    location = location,
                    date = date,
                    category = category,
                    isAccepted = true //mise par défaut à true pour qu'il s'affiche direct
                )
                onAddEvent(newEvent)
            }) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onDismiss() }) {
                Text("Annuler")
            }
        },
        title = { Text("Nouvel événement") },
        text = { //champs à renseigner
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Lieu") })
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (ex: 06 avril 2025)") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Catégorie") })
            }
        }
    )
}



@Composable
fun analyzeWithAI(
    eventViewModel: EventViewModel,
    onFinished: () -> Unit // <- Callback une fois l’analyse finie
) {
    val geminiViewModel: Gemini = viewModel()
    val events by eventViewModel.acceptedEvents.observeAsState(emptyList())

    val formattedEvents = events.joinToString("\n") {
        "• ${it.title} le ${it.date},(${it.category})"
    }

    val prompt = """
        Voici mon agenda :
        $formattedEvents

        Ai-je besoin de repos ou d'équilibre ?
        Fais moi une réponse courte et concrète stp.
    """.trimIndent()

    var aiResponse by remember { mutableStateOf<String?>(null) }

    // Appel à l'IA
    LaunchedEffect(prompt) {
        geminiViewModel.analysePlanning(prompt) { result ->
            aiResponse = result
        }
    }

    // Affichage du résultat Si aiResponse n’est pas nul
    aiResponse?.let { response ->
        showAdviceDialog(
            aiResponse = response,
            onDismiss = {
                aiResponse = null // Nettoyage
                onFinished() // <- Indique que l'affichage est fini
            }
        )
    }
}




@Composable
fun showAdviceDialog(
    aiResponse: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Conseil de l'Assistant IA")
        },
        text = {
            Text(text = aiResponse)
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}
