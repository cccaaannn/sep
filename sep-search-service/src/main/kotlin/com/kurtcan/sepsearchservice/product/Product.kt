package com.kurtcan.sepsearchservice.product

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.kurtcan.sepsearchservice.base.BaseEntity
import java.math.BigDecimal

data class Product @JsonCreator constructor(
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("price") val price: BigDecimal,
    @JsonProperty("stockAmount") var stockAmount: Int
) : BaseEntity()