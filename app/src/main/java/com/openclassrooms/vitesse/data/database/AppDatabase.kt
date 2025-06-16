package com.openclassrooms.vitesse.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.vitesse.data.converter.Converters
import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.data.dto.CandidateDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [CandidateDto::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
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
            sleepDao: CandidateDao
        ) {


            sleepDao.insertCandidate(
                CandidateDto(
                    id = 1,
                    firstname = "Arno",
                    lastname = "Bouiron",
                    phone = "+33 6 26 23 25 58",
                    email = "arno.bouiron@gmail.com",
                    birthdate = "09/12/1993",
                    salary = 2000.0,
                    notes = "Bla bla bla"
                )
            )
            sleepDao.insertCandidate(
                CandidateDto(
                    id = 1,
                    firstname = "Diane",
                    lastname = "Bouiron",
                    phone = "+33 6 16 44 32 24",
                    email = "dbouiron@gmail.com",
                    birthdate = "26/05/2000",
                    salary = 2450.0,
                    notes = "You pla la"
                )
            )
        }
    }

}