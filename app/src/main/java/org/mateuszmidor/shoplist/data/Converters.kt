package org.mateuszmidor.shoplist.data

import androidx.room3.ColumnTypeConverter
import java.util.UUID

class Converters {

    @ColumnTypeConverter
    fun fromUuid(uuid: UUID): String = uuid.toString()

    @ColumnTypeConverter
    fun toUuid(value: String): UUID = UUID.fromString(value)
}