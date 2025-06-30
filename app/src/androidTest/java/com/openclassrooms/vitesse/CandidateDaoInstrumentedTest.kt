package com.openclassrooms.vitesse

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.data.database.AppDatabase
import com.openclassrooms.vitesse.data.dto.CandidateDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CandidateDaoInstrumentedTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: CandidateDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.candidateDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndGetCandidateById() = runTest {
        val candidate = CandidateDto(
            id = 1,
            firstname = "Bob",
            lastname = "Martin",
            phone = "phone",
            email = "email",
            birthdate = "birth",
            salary = 1000,
            notes = null,
            profilePicture = null,
            isFavorite = true
        )

        val insertedId = dao.insertCandidate(candidate)
        val retrieved = dao.getCandidateById(insertedId)

        assertEquals(candidate, retrieved)
    }

    @Test
    fun insertAndDeleteCandidateById() = runTest {
        val candidate = CandidateDto(
            id = 1,
            firstname = "Bob",
            lastname = "Martin",
            phone = "phone",
            email = "email",
            birthdate = "birth",
            salary = 1000,
            notes = null,
            profilePicture = null,
            isFavorite = true
        )

        val insertedId = dao.insertCandidate(candidate)

        dao.deleteCandidateById(insertedId)

        val result = dao.getCandidateByIdOrNull(insertedId)

        assertNull(result)

    }

    @Test
    fun getAllFavoritesCandidates() = runTest {
        val candidate = CandidateDto(
            id = 1,
            firstname = "Bob",
            lastname = "Martin",
            phone = "phone",
            email = "email",
            birthdate = "birth",
            salary = 1000,
            notes = null,
            profilePicture = null,
            isFavorite = true
        )

        dao.insertCandidate(candidate)

        val favorites = dao.getAllFavoriteCandidates(true).first()

        assertEquals(1, favorites.size)
        assertEquals(candidate, favorites.first())
    }

}


