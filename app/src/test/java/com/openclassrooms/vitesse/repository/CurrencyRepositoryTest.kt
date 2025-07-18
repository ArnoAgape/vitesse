package com.openclassrooms.vitesse.repository

import com.openclassrooms.vitesse.data.network.CurrencyApiService
import com.openclassrooms.vitesse.data.repository.CurrencyRepository
import com.openclassrooms.vitesse.ui.detail.EuroToCurrencyResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CurrencyRepositoryTest {

    private val api: CurrencyApiService = mockk()
    private lateinit var repo: CurrencyRepository

    @Before
    fun setup() {
        repo = CurrencyRepository(api)
    }

    @Test
    fun `get euro amount converted into gbp`() = runTest {

        val gbp = EuroToCurrencyResponse(mapOf("gbp" to 0.89))

        coEvery { api.getGbpRate() } returns gbp

        // Act
        val result = repo.getEuroToGbpRate()

        // Assert
        assertEquals(0.89, result)

    }

    @Test
    fun `get euro amount converted into gbp with no value`() = runTest {

        val gbp = EuroToCurrencyResponse(mapOf())

        coEvery { api.getGbpRate() } returns gbp

        // Act
        val result = repo.getEuroToGbpRate()

        // Assert
        assertEquals(null, result)

    }
}