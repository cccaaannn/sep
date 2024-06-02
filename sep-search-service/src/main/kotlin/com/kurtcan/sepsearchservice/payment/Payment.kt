package com.kurtcan.sepsearchservice.payment

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.kurtcan.sepsearchservice.base.BaseEntity
import java.math.BigDecimal
import java.util.*

data class Payment @JsonCreator constructor(
    @JsonProperty("userId") val userId: UUID,
    @JsonProperty("productId") val productId: UUID,
    @JsonProperty("amount") val amount: Int,
    @JsonProperty("price") val price: BigDecimal
) : BaseEntity()