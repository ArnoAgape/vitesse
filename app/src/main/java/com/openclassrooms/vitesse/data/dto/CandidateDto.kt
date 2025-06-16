package com.openclassrooms.vitesse.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidate")
data class CandidateDto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,


    @ColumnInfo(name = "firstname")
    var firstname: String,

    @ColumnInfo(name = "lastname")
    var lastname: String,

    @ColumnInfo(name = "phone")
    var phone: String,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "birthdate")
    var birthdate: String,

    @ColumnInfo(name = "salary")
    var salary: Double,

    @ColumnInfo(name = "notes")
    var notes: String

)