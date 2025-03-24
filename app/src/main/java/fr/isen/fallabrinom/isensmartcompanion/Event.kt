package fr.isen.fallabrinom.isensmartcompanion

/*data class Event(
    val id: String,
    val title: String,
    val description: String,
    val date: String, // Par exemple "2025-03-25 14:30"
    val location: String,
    val category: String,
    val isAccepted: Boolean = false // Indique si l'événement a été accepté ou non
)*/

data class Event(
    val category: String,
    val date: String,
    val description: String,
    val id: String,
    val location: String,
    val title:String,
    val isAccepted: Boolean = false
)