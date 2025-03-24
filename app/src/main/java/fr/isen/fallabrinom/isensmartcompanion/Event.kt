package fr.isen.fallabrinom.isensmartcompanion

data class Event(
    val category: String,
    val date: String,
    val description: String,
    val id: String,
    val location: String,
    val title:String,
    val isAccepted: Boolean = false
)