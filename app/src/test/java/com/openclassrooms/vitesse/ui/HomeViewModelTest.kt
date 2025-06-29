package com.openclassrooms.vitesse.ui


import app.cash.turbine.test
import com.openclassrooms.vitesse.data.repository.CandidateRepository
import com.openclassrooms.vitesse.domain.model.Candidate
import com.openclassrooms.vitesse.states.State
import com.openclassrooms.vitesse.ui.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
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

            emit(Result.success(listOf(
                Candidate(1, "David", "Bowie", "phone", "email", "birth",
                    1000, null),
                Candidate(2, "Bob", "Marley", "phone", "email", "birth",
                    1000, null),
                Candidate(3, "Charlie", "Chocolate", "phone", "email", "birth",
                    1000, null)
            )))
        }

        coEvery { repo.getAllFavoriteCandidates() } returns flow {
            emit(Result.success(listOf(
                Candidate(1, "Alice", "Wonderland", "phone", "email", "birth",
                    1000, null, isFavorite = true)
            )))
        }

        viewModel = HomeViewModel(repo)

    }

    @Test
    fun `uiState emits Loading then Success`() = runTest {
        viewModel.uiState.test {

            val loading = awaitItem()
            assertEquals(State.Loading, loading.result)

            val success = awaitItem()
            assertEquals(State.Success, success.result)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `displayedCandidatesFlow filters candidates by search query`() = runTest {

        viewModel.onSearchChange("Bob")

        viewModel.displayedCandidatesFlow.test {
            awaitItem()
            val filtered = awaitItem()

            assertEquals(1, filtered.size)
            assertEquals("Bob", filtered.first().firstname)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `displayedCandidatesFlow shows favorites`() = runTest {

        viewModel.toggleFavorites(true)

        viewModel.displayedCandidatesFlow.test {
            awaitItem()
            val filtered = awaitItem()

            assertEquals(1, filtered.size)
            assertEquals(true, filtered.first().isFavorite)

            cancelAndIgnoreRemainingEvents()
        }
    }

}