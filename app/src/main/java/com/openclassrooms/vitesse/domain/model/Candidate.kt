package com.openclassrooms.vitesse.domain.model

import com.openclassrooms.vitesse.data.dto.CandidateDto

data class Candidate(
    val id: Long,
    var firstname: String,
    var lastname: String,
    var phone: String,
    var email: String,
    var birthdate: String,
    var salary: Double,
    var notes: String
) {

    fun toDto(): CandidateDto {
        return CandidateDto(
            id = id,
            firstname = firstname,
            lastname = lastname,
            phone = phone,
            email = email,
            birthdate = birthdate,
            salary = salary,
            notes = notes
        )
    }

    companion object {
        fun fromDto(dto: CandidateDto): Candidate {
            return Candidate(
                id = dto.id,
                firstname = dto.firstname,
                lastname = dto.lastname,
                phone = dto.phone,
                email = dto.email,
                birthdate = dto.birthdate,
                salary = dto.salary,
                notes = dto.notes
            )
        }
    }
}