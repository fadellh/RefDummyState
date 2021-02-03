package com.example.feature_home.data.api.service

import com.example.feature_home.data.api.request.RefferalListRequest
import com.example.feature_home.data.api.response.ReferralResMoshi
import com.example.feature_home.data.api.response.RefferalListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RefferalService {

    @GET("/api/v1/allRef")
    suspend fun getAllRefferal() : Response<List<RefferalListResponse>>

    @GET("/api/v1/allRef")
    suspend fun getAllRefferalAsState() : Response<List<RefferalListResponse>>

    @GET("/api/v1/allRef")
    suspend fun getAllRefferalAsDoctor() : List<ReferralResMoshi>

    @GET("/api/v1/allRef")
    suspend fun getAllReferralAsDoctor() : List<RefferalListResponse>

    @POST("/api/v1/allRef")
    suspend fun createPatient(
            @Body request: RefferalListRequest
    ) : Response<RefferalListResponse>

}