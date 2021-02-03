package com.example.feature_home.data.api.response

import com.example.feature_home.data.domain.RefferalList
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReferralResMoshi(
    @Json(name = "id")
    val id : Int,
    @Json(name = "patient_name")
    val patientName : String,
    @Json(name = "status")
    val status : Int
)

fun ReferralResMoshi.toDomainMoshi() =
    RefferalList(
        patient_name = patientName,
        status = status
    )

//Moshi
internal fun ReferralResMoshi.toEntityMoshi() =
    com.example.feature_home.data.persistance.entity.RefferalList(
        id = id,
        patientName = patientName,
        status = status
    )

