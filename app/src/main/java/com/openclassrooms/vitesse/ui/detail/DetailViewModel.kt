package com.openclassrooms.vitesse.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesse.data.repository.CandidateRepository
import com.openclassrooms.vitesse.data.repository.CurrencyRepository
import com.openclassrooms.vitesse.domain.model.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: CandidateRepository,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _candidateFlow = MutableStateFlow<Candidate?>(null)
    val candidateFlow: StateFlow<Candidate?> = _candidateFlow.asStateFlow()

    private val _gbpFlow = MutableStateFlow<Double?>(null)
    val gbpFlow: StateFlow<Double?> = _gbpFlow.asStateFlow()

    private val _errorFlow = MutableStateFlow<String?>(null)
    val errorFlow: StateFlow<String?> = _errorFlow.asStateFlow()

    fun getEuroConverted() {
        viewModelScope.launch {
            try {
                val gbpRate = currencyRepository.getEuroToGbpRate()
                _gbpFlow.value = gbpRate
                Log.d("Currency", "1 EUR = $gbpRate GBP")
            } catch (e: Exception) {
                _errorFlow.value = "API Error : ${e.message}"
            }
        }
    }

    fun getCandidateById(id: Long) {
        viewModelScope.launch {
            val result = repository.getCandidate(id)
            if (result.isSuccess) {
                _candidateFlow.value = result.getOrNull()
            } else {
                _errorFlow.value =
                    result.exceptionOrNull()?.message ?: "Error while collecting the candidate"
            }
        }
    }

    fun deleteCandidate(candidate: Candidate) {
        viewModelScope.launch {
            repository.deleteCandidate(candidate)
        }
    }

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(id, isFavorite)
        }
    }


}