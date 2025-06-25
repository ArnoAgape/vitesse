package com.openclassrooms.vitesse.repository

import app.cash.turbine.test
import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.data.dto.CandidateDto
import com.openclassrooms.vitesse.data.repository.CandidateRepository
import com.openclassrooms.vitesse.domain.model.Candidate
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class CandidateRepositoryTest {

    private val dao: CandidateDao = mockk()
    private lateinit var repo: CandidateRepository

    @Before
    fun setup() {
        repo = CandidateRepository(dao)
    }


    @Test
    fun `getAllCandidates emits success with mapped candidates`() = runTest {

        // Arrange
        val candidateDto = CandidateDto(
            id = 1,
            firstname = "martin",
            lastname = "dupond",
            phone = "0606060606",
            email = "test@mail.fr",
            birthdate = "03/10/2000",
            salary = 2000,
            notes = "best candidate",
            profilePicture = "",
            isFavorite = true
        )
        val expected = Candidate.fromDto(candidateDto)
        val flow = flowOf(listOf(candidateDto))

        coEvery { dao.getAllCandidates() } returns flow

        // Act
        repo.getAllCandidates().test {
            val result = awaitItem()

            // Assert
            assertTrue(result.isSuccess)
            assertEquals(listOf(expected), result.getOrNull())

            cancelAndIgnoreRemainingEvents()
        }
    }
}