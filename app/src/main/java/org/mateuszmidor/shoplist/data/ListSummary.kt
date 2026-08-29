package org.mateuszmidor.shoplist.data

import java.util.UUID

data class ListSummary(
    val id: UUID,
    val name: String,
    val createdAt: Long,
    val totalCount: Int,
    val boughtCount: Int,
)
