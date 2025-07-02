package com.openclassrooms.vitesse.ui

import app.cash.turbine.test
import com.openclassrooms.vitesse.data.repository.CandidateRepository
import com.openclassrooms.vitesse.domain.model.Candidate
import com.openclassrooms.vitesse.ui.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class HomeViewModelTest {

    private lateinit var repo: CandidateRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        repo = mockk()

        coEvery { repo.getAllCandidates() } returns flow {
            emit(
                Result.success(
                    listOf(
                        Candidate(
                            1, "David", "Bowie", "phone",
                            "email", "birth",
                            1000, null, isFavorite = false
                        ),
                        Candidate(
                            2, "Bob", "Marley", "phone",
                            "email", "birth",
                            1000, null, isFavorite = false
                        ),
                        Candidate(
                            3, "Charlie", "Chocolate", "phone",
                            "email", "birth",
                            1000, null, isFavorite = false
                        ),
                        Candidate(
                            4, "Alice", "Wonderland", "phone",
                            "email", "birth",
                            1000, null, isFavorite = true
                        )
                    )
                )
            )
        }

        coEvery { repo.getAllFavoriteCandidates() } returns flow {
            emit(
                Result.success(
                    listOf(
                        Candidate(
                            4, "Alice", "Wonderland", "phone",
                            "email", "birth",
                            1000, null, isFavorite = true
                        )
                    )
                )
            )
        }
        viewModel = HomeViewModel(repo)
    }

    @Test
    fun `uiState shows all candidates when showFavorites is false`() = runTest {
        viewModel.uiState.test {
            skipItems(3)

            val filtered = awaitItem()

            println("🔍 $filtered")

            assertEquals(4, filtered.candidate.size) // David, Bob, Charlie
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState shows only favorite candidates when showFavorites is true`() = runTest {
        viewModel.toggleFavorites(true)

        viewModel.uiState.test {
            skipItems(3) // ignore Idle + Success([])
            val filtered = awaitItem() // Success([Alice])

            println("🔍 $filtered")

            assertEquals(1, filtered.candidate.size)
            assertTrue(filtered.candidate.first().isFavorite)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
