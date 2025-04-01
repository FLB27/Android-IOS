package fr.isen.fallabrinom.isensmartcompanion
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events") //on indique que la classe est une entité de notre base de donnée
data class Event(
    @PrimaryKey() val id: String = "",
    val category: String,
    val date: String,
    val description: String,
    val location: String,
    val title:String,
    val isAccepted: Boolean = false
)