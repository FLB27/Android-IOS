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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.fallabrinom.isensmartcompanion.Event
import fr.isen.fallabrinom.isensmartcompanion.event.EventViewModel
import io.wojciechosak.calendar.animation.CalendarAnimator
import io.wojciechosak.calendar.config.rememberCalendarState
import io.wojciechosak.calendar.view.CalendarView
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import kotlinx.datetime.*
import kotlinx.datetime.format.DateTimeFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter


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
    calendarView: @Composable (monthOffset: Int) -> Unit = { monthOffset ->
        val convertedDate = Instant.fromEpochMilliseconds(newDate)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        CalendarView(
            day = { dayState ->
                val isMarked = markedDates.contains(dayState.date) //si une des dates de la liste concorde avec celle du tableau
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
                        .clickable {// Convertir la date au format millisecondes et la passer au callback
                            val selectedDateMillis = dayState.date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                            onDateSelected(selectedDateMillis)  // Met à jour la date sélectionnée lors du clic
                        }, // Met à jour la date sélectionnée lors du clic
                    contentAlignment = Alignment.Center
                ) {
                    Text(dayState.date.dayOfMonth.toString())
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
            modifier = Modifier.fillMaxWidth().padding(16.dp)
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
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgendaScreen(modifier: Modifier, eventViewModel: EventViewModel) {
    val currentDate: LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }

    // Événements validés
    val validevents by eventViewModel.acceptedEvents.observeAsState(emptyList())

    // Animation calendrier
    val calendarAnimator = remember { mutableStateOf(CalendarAnimator(currentDate)) }

    // Conversion de la date sélectionnée en LocalDate
    val selectedLocalDate = Instant.fromEpochMilliseconds(selectedDate)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date //	selectedLocalDate : C’est la date que tu sélectionnes, par exemple en cliquant sur une date dans le calendrier.

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

    // Filtrer les événements à la date sélectionnée
    val filteredEvents = validevents.filter {
        try {
            val eventFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH)
            val eventDate = java.time.LocalDate.parse(it.date, eventFormatter)
            LocalDate(eventDate.year, eventDate.monthValue, eventDate.dayOfMonth) == selectedLocalDate
        } catch (e: Exception) {
            false
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
            onDateSelected = { newSelectedDate -> selectedDate = newSelectedDate }
        )

        if (filteredEvents.isEmpty()) {
            Text("Aucun événement ce jour.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp) // Espacement entre les éléments de la LazyColumn
            ) {
                items(filteredEvents) { event ->
                    EventItem(event,modifier)
                }
            }
        }
    }
}




@Composable
fun EventItem(event: Event,modifier: Modifier) {
    // Affichage sous forme de carte (Card) pour chaque événement
    Card(
        modifier
            //.padding(8.dp) // Ajoute un peu d'espace autour de chaque carte
            .fillMaxSize(),
        //elevation = 4.dp // Donne une légère élévation à chaque carte
    ) {
        Column{
            // Affichage du titre de l'événement
            Text(
                text = event.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // Affichage de la date de l'événement
            Text(
                text = event.date,
            )

            // Affichage de la description de l'événement
            Text(
                text = event.description,
            )

            // Optionnel : Ajouter un bouton pour interagir avec l'événement
            Button(
                onClick = { /* Interaction utilisateur ici, par exemple, marquer l'événement comme terminé */ },
                //modifier.padding(top = 0.dp)
            ) {
                Text("Evènement fini")
            }
        }
    }
}