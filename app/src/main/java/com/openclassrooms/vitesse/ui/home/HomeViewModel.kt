package com.openclassrooms.vitesse.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesse.data.repository.CandidateRepository
import com.openclassrooms.vitesse.domain.model.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CandidateRepository
) : ViewModel() {

    private val _candidatesFlow = MutableStateFlow<List<Candidate>>(emptyList())
    val candidatesFlow: StateFlow<List<Candidate>> = _candidatesFlow.asStateFlow()

    private val _favoriteCandidatesFlow = MutableStateFlow<List<Candidate>>(emptyList())
    val favoriteCandidatesFlow: StateFlow<List<Candidate>> = _favoriteCandidatesFlow.asStateFlow()

    private val _errorFlow = MutableStateFlow<String?>(null)
    val errorFlow: StateFlow<String?> = _errorFlow.asStateFlow()

    init {
        loadAllCandidates()
    }

    private fun loadAllCandidates() {
        viewModelScope.launch {
            repository.getAllCandidates().collect { result ->
                if (result.isSuccess) {
                    _candidatesFlow.value = result.getOrNull() ?: emptyList()
                } else {
                    _errorFlow.value = result.exceptionOrNull()?.message ?: "Error loading the candidates"
                }
            }
        }
    }

    private fun loadAllFavoriteCandidates() {
        viewModelScope.launch {
            repository.getAllFavoriteCandidates().collect { result ->
                if (result.isSuccess) {
                    _favoriteCandidatesFlow.value = result.getOrNull() ?: emptyList()
                }
            }
        }
    }

    fun addNewCandidate(candidate: Candidate) {
        viewModelScope.launch {
            val result = repository.addCandidate(candidate)
            if (result.isSuccess) {
                loadAllCandidates()
            } else {
                _errorFlow.value = result.exceptionOrNull()?.message ?: "Error while adding a candidate"
            }
        }
    }
}