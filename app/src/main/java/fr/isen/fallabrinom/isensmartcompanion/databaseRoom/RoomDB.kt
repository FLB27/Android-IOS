package fr.isen.fallabrinom.isensmartcompanion.databaseRoom

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

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

@Database(entities = [History::class], version = 1) //création d'une BDD de type History avec les DAO pour qu'on puisse communiquer avec
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
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


