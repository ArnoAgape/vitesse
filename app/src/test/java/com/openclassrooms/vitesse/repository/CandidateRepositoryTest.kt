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
            lastname = "pond",
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

    @Test
    fun `getAllFavoriteCandidates emits success with mapped candidates`() = runTest {

        // Arrange
        val candidateDto = CandidateDto(
            id = 1,
            firstname = "martin",
            lastname = "pond",
            phone = "0606060606",
            email = "test@mail.fr",
            birthdate = "03/10/2000",
            salary = 2000,
            notes = "best candidate",
            profilePicture = "",
            isFavorite = false
        )
        val expected = Candidate.fromDto(candidateDto)
        val flow = flowOf(listOf(candidateDto))

        coEvery { dao.getAllFavoriteCandidates(true) } returns flow

        // Act
        repo.getAllFavoriteCandidates().test {
            val result = awaitItem()

            // Assert
            assertTrue(result.isSuccess)
            assertEquals(listOf(expected), result.getOrNull())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCandidate returns success when DAO works`() = runTest {
        // Arrange
        val candidateDto = Candidate(
            id = 1,
            firstname = "martin",
            lastname = "pond",
            phone = "0606060606",
            email = "test@mail.fr",
            birthdate = "03/10/2000",
            salary = 2000,
            notes = "best candidate",
            profilePicture = "",
            isFavorite = true
        )

        coEvery { dao.getCandidateById(1) } returns candidateDto.toDto()

        // Act
        val result = repo.getCandidate(1)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(candidateDto, result.getOrNull())
    }

    @Test
    fun `getCandidate returns error when DAO fails`() = runTest {
        // Arrange
        coEvery { dao.getCandidateById(1) } throws Exception("error")

        // Act
        val result = repo.getCandidate(1)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addCandidate returns success when DAO works`() = runTest {
        // Arrange
        val candidateDto = CandidateDto(
            id = 1,
            firstname = "martin",
            lastname = "pond",
            phone = "0606060606",
            email = "test@mail.fr",
            birthdate = "03/10/2000",
            salary = 2000,
            notes = "best candidate",
            profilePicture = "",
            isFavorite = true
        )

        val expected = Candidate.fromDto(candidateDto)
        coEvery { dao.insertCandidate(candidateDto) } returns 1

        // Act
        val result = repo.addCandidate(expected)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `addCandidate returns error when DAO fails`() = runTest {
        // Arrange
        val candidateDto = CandidateDto(
            id = 1,
            firstname = "martin",
            lastname = "pond",
            phone = "0606060606",
            email = "test@mail.fr",
            birthdate = "03/10/2000",
            salary = 2000,
            notes = "best candidate",
            profilePicture = "",
            isFavorite = true
        )

        val expected = Candidate.fromDto(candidateDto)
        coEvery { dao.insertCandidate(candidateDto) } throws Exception("error")

        // Act
        val result = repo.addCandidate(expected)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `updateCandidate returns success when DAO works`() = runTest {
        // Arrange
        val candidateDto = CandidateDto(
            id = 1,
            firstname = "martin",
            lastname = "pond",
            phone = "0606060606",
            email = "test@mail.fr",
            birthdate = "03/10/2000",
            salary = 2000,
            notes = "best candidate",
            profilePicture = "",
            isFavorite = true
        )

        val expected = Candidate.fromDto(candidateDto)
        coEvery { dao.updateCandidate(candidateDto) } returns Unit

        // Act
        val result = repo.updateCandidate(expected)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `updateFavorite returns success when DAO works`() = runTest {
        // Arrange

        coEvery { dao.updateFavorite(1, true) } returns Unit

        // Act
        val result = repo.updateFavorite(1, true)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `deleteCandidate returns success when DAO works`() = runTest {
        // Arrange
        val candidateDto = CandidateDto(
            id = 1,
            firstname = "martin",
            lastname = "pond",
            phone = "0606060606",
            email = "test@mail.fr",
            birthdate = "03/10/2000",
            salary = 2000,
            notes = "best candidate",
            profilePicture = "",
            isFavorite = true
        )

        val expected = Candidate.fromDto(candidateDto)
        coEvery { dao.deleteCandidateById(1) } returns Unit

        // Act
        val result = repo.deleteCandidate(expected)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

}