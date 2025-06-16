package com.openclassrooms.vitesse.data.repository

import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.domain.model.Candidate
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class CandidateRepository(private val candidateDao: CandidateDao) {

    // Get all candidates
    suspend fun getAllCandidates(): Result<List<Candidate>> {
        return withContext(Dispatchers.IO) {
            try {
                val list = candidateDao.getAllCandidates()
                    .map { Candidate.fromDto(it) }
                Result.success(list)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Add a new candidate
    suspend fun addCandidate(candidate: Candidate): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                candidateDao.insertCandidate(candidate.toDto())
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Delete a candidate
    suspend fun deleteCandidate(candidate: Candidate): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                candidate.id.let {
                    candidateDao.deleteCandidateById(it)
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Edit a candidate
    suspend fun editCandidate(candidate: Candidate): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                candidate.id.let {
                    candidateDao.updateCandidates()
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}