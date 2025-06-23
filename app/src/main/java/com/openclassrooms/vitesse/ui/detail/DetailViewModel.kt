package com.openclassrooms.vitesse.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesse.data.repository.CandidateRepository
import com.openclassrooms.vitesse.di.NetworkModule.provideEurConversion
import com.openclassrooms.vitesse.domain.model.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: CandidateRepository
) : ViewModel() {

    private val _candidateFlow = MutableStateFlow<Candidate?>(null)
    val candidateFlow: StateFlow<Candidate?> = _candidateFlow.asStateFlow()

    private val _errorFlow = MutableStateFlow<String?>(null)
    val errorFlow: StateFlow<String?> = _errorFlow.asStateFlow()

    fun getEurConverted() {
        viewModelScope.launch {
            try {
                val resp = provideEurConversion.getEurRates()
                val gbpRate = resp.eur["gbp"]
                Log.d("Currency", "1 EUR = $gbpRate GBP")
            } catch (e: Exception) {
                Log.e("Currency", "Erreur API : ${e.message}")
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

}