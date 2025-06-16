package com.openclassrooms.vitesse.data.converter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {

    // LocalDateTime <-> Long
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime): Long =
        value.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDateTime(value: Long): LocalDateTime =
        Instant.ofEpochMilli(value).atZone(ZoneOffset.UTC).toLocalDateTime()
}