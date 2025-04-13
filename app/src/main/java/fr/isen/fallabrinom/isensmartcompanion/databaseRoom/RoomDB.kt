package fr.isen.fallabrinom.isensmartcompanion.databaseRoom

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.isen.fallabrinom.isensmartcompanion.Event

@Entity
data class History(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // Ajout de autoGenerate pour créer automatiquement un ID //clé primaire de chaque objet de cette classe
    @ColumnInfo(name = "user_message") val user: String?,
    @ColumnInfo(name = "bot_message") val bot: String?,
    @ColumnInfo(name = "date") val date: String?
)


@Dao //on crée les DAO donc toutes les commandes qu'on va vouloir utiliser via des méthpdes pour que la bdd nous comprenne
interface UserDao {
    @Query("SELECT * FROM History") //à cette commande BDD on associe une méthode
    fun getAll(): LiveData<List<History>> // Retourne un LiveData pour une mise à jour en temps réel

    @Insert
    suspend fun insertAll(vararg history : History)

    @Delete
    suspend fun delete(history: History)

    @Query("DELETE FROM History")
     suspend fun deleteAll() // Supprimer tout l’historique

}

//Events gestion
@Dao //on crée les DAO donc toutes les commandes qu'on va vouloir utiliser via des méthpdes pour que la bdd nous comprenne
interface EventDao {
    @Query("SELECT * FROM events") //à cette commande BDD on associe une méthode
    fun getAllEvent(): LiveData<List<Event>> //Fonction suspendue pour éviter de bloquer l’UI

    @Query("SELECT * FROM events WHERE isAccepted = 1 ORDER BY date ASC")
    fun getAcceptedEvents(): LiveData<List<Event>> //gère les événements qui sont acceptés

    @Query("SELECT * FROM events WHERE isAccepted = 0 ORDER BY date ASC")
    suspend fun getUnAcceptedEvents(): List<Event> //gère les événements qui sont pas acceptés

    @Insert(onConflict = OnConflictStrategy.REPLACE)//Permet d’écraser les anciens événements avec les nouveaux
    suspend fun insertAllEvent(event: List<Event>)

    @Insert
    suspend fun insertOneEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("UPDATE events SET isAccepted = :isAccepted WHERE id = :eventId")
    suspend fun updateEventAcceptance(eventId: String, isAccepted: Boolean)

    @Query("SELECT COUNT(*) FROM events") //permet de compter le nombre d'élément présent dans la liste
    suspend fun getEventCount(): Int

    @Query("SELECT COUNT(*) FROM events WHERE isAccepted = 0") //permet de compter le nombre d'élément présent dans la liste
    suspend fun getUnAcceptedEventCount(): Int


}

@Database(entities = [History::class, Event::class], version = 1, exportSchema = false) //création d'une BDD de type History avec les DAO pour qu'on puisse communiquer avec
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
}

object DatabaseManager { //on crée un singleton pour garder une seule instance partagée entre tous les composants de l’application.

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) { //synchronised permet de ne créer qu'une seule instance
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "history_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}

/*
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `events` (
                    `Id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `title` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `date` INTEGER NOT NULL,
                `location` TEXT,
                `isAccepted` INTEGER NOT NULL DEFAULT 0
            )"
        )
    }
}*/





