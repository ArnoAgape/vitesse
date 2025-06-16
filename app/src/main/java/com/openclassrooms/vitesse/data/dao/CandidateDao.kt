package com.openclassrooms.vitesse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.vitesse.data.dto.CandidateDto
import com.openclassrooms.vitesse.domain.model.Candidate

@Dao
interface CandidateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidate(candidate: CandidateDto): Long

    @Query("SELECT * FROM candidate")
    suspend fun getAllCandidates(): List<CandidateDto>

    @Delete
    suspend fun deleteCandidateById(id: Long)

    @Update
    suspend fun updateCandidates(vararg candidates: Candidate)
}