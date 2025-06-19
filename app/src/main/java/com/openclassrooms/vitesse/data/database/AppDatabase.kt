package com.openclassrooms.vitesse.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.data.dto.CandidateDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [CandidateDto::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun candidateDao(): CandidateDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(
                        database.candidateDao()
                    )
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null


        fun getDatabase(context: Context, coroutineScope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val firstInstance = INSTANCE
                if (firstInstance != null) return firstInstance
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "VitesseDB"
                )
                    .fallbackToDestructiveMigration(true)
                    .addCallback(AppDatabaseCallback(coroutineScope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateDatabase(
            candidateDao: CandidateDao
        ) {

            candidateDao.insertCandidate(
                CandidateDto(
                    id = 0,
                    firstname = "John",
                    lastname = "Doe",
                    phone = "+33 6 19 35 40 58",
                    email = "jdoe@mail.fr",
                    birthdate = "09/12/1993",
                    salary = 2000.0,
                    notes = "Very good candidate",
                    isFavorite = true
                )
            )
            candidateDao.insertCandidate(
                CandidateDto(
                    id = 1,
                    firstname = "Martin",
                    lastname = "Dupond",
                    phone = "+33 6 16 44 32 24",
                    email = "mdupond@mail.fr",
                    birthdate = "26/05/2000",
                    salary = 2450.0,
                    notes = "May hire him",
                    isFavorite = false
                )
            )
        }
    }
}