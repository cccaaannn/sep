package com.kurtcan.sepsearchservice.base

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.kurtcan.sepsearchservice.shared.elasticsearch.ElasticsearchEntity
import com.kurtcan.sepsearchservice.shared.entity.DbEntity
import java.time.Instant
import java.util.*

open class BaseEntity @JsonCreator constructor(
    @JsonProperty("id") private var id: UUID? = null,
    @JsonProperty("createdAt") private var createdAt: Instant? = null,
    @JsonProperty("updatedAt") private var updatedAt: Instant? = null
) : ElasticsearchEntity, DbEntity {
    override fun getId(): UUID {
        return id!!
    }

    fun setId(newId: UUID) {
        id = newId
    }

    override fun getCreatedAt(): Instant {
        return createdAt!!
    }

    fun setCreatedAt(newCreatedAt: Instant) {
        createdAt = newCreatedAt
    }

    override fun getUpdatedAt(): Instant {
        return updatedAt!!
    }

    fun setUpdatedAt(newUpdatedAt: Instant) {
        updatedAt = newUpdatedAt
    }
}