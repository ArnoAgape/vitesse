package com.openclassrooms.vitesse.data.repository

import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.data.network.CurrencyApiService
import com.openclassrooms.vitesse.domain.model.Candidate
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class CandidateRepository(private val candidateDao: CandidateDao,
    private val currencyApiService: CurrencyApiService) {

    // Get all candidates
    fun getAllCandidates(): Flow<Result<List<Candidate>>> {
        return candidateDao.getAllCandidates()
            .map { dtoList ->
                val domainList = dtoList.map { Candidate.fromDto(it) }
                Result.success(domainList)
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    // Get all favorite candidates
    fun getAllFavoriteCandidates(): Flow<Result<List<Candidate>>> {
        return candidateDao.getAllFavoriteCandidates(true)
            .map { dtoList ->
                val domainList = dtoList.map { Candidate.fromDto(it) }
                Result.success(domainList)
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    // Get one candidate by id
    suspend fun getCandidate(id: Long): Result<Candidate> {
        return withContext(Dispatchers.IO) {
            try {
                val user = candidateDao.getCandidateById(id)
                    .let { Candidate.fromDto(it) }
                Result.success(user)
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
    suspend fun updateCandidate(candidate: Candidate): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                candidateDao.updateCandidate(candidate.toDto())
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}