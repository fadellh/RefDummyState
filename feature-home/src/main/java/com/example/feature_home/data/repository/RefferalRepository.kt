package com.example.feature_home.data.repository

import android.util.Log
import com.example.feature_home.data.api.*
import com.example.feature_home.data.api.ApiResult
import com.example.feature_home.data.api.request.RefferalListRequest
import com.example.feature_home.data.api.response.*
import com.example.feature_home.data.api.response.toEntity
import com.example.feature_home.data.api.service.RefferalService
import com.example.feature_home.data.persistance.dao.RefferalDao
import com.example.feature_home.data.persistance.entity.RefferalList
import com.example.feature_home.data.persistance.entity.toDomain
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject
internal class RefferalRepository internal constructor(
    private val referralDao:RefferalDao
    ,private val apiExecutor: ApiExecutor
    ,private val apiService: RefferalService
){


    suspend fun insertReferral(ref:RefferalList) = referralDao.insertRefferal(ref)

    suspend fun deleteReferral(ref:RefferalList) = referralDao.insertRefferal(ref)

   /* suspend fun getAllReferral() : Response<List<com.example.feature_home.data.domain.RefferalList>> {
        val result = referralDao.getAllRefferal()
        //return (result.body()?.map { it.toDomain()})
    }*/

    suspend fun replaceAllReferral(ref: List<RefferalList>) = referralDao.replaceAllReferral(ref)

    fun getAllReferralAsFlow() = referralDao.getAllRefferalAsFlow().map { it.map { res -> res.toDomain() } }

    suspend fun getAllListReferral() : Response<List<com.example.feature_home.data.domain.RefferalList>> {
        val result =  ApiClient.getReferralListApiGson.getAllRefferal()
       // val results =  ApiClient.getReferralListApiGson.getAllRefferal().enqueue(Callback)

            val codeSuccess = Response.success(result.code())
            val isCodeSuccess = Response.success(result.isSuccessful).code()
            val code = Response.success(result.code(),result.body())

            if(isCodeSuccess < 300){
                Log.d("masuk pak eko","masuk pake eko")
                //  val response = Response.success(result.body()
                val hasil = result.body()!!.map { it.toEntity()}
                replaceAllReferral(hasil)
                return Response.success(result.body()?.map { it.toDomain()})
            }else{
                Log.d("errorrrr","Hai")
                return Response.success(result.body()?.map { it.toDomain()})
            }
        }

    suspend fun getAllListReferralAsState() : Flow<Resource<List<com.example.feature_home.data.domain.RefferalList>>> = flow {
     //   val result = callApi.body()?.map { it.toDomain() }
      runCatching {
          val callApi = ApiClient.getReferralListApiGson.getAllRefferalAsState()
          val resource : Resource<List<com.example.feature_home.data.domain.RefferalList>> = when(callApi.isSuccessful){
              true -> {
                  callApi.body().let { res ->
                      val hasil = res!!.map { it.toDomain() }
                      Resource.Success(hasil).also {
                          replaceAllReferral(res.map { it.toEntity() })
                      }
                  }
              }
              //true -> with(callApi.body())
              else -> Resource.Error(callApi.message())
          }
          emit(resource)
      }.onFailure {
          Log.d("coabres","halo failure")
      }
        /*
          emit(resource)

               if(result.isSuccessful) {
                   result.body()?.let { resultResponse ->
                        Resource.Success(resultResponse.map { it.toDomain() })
                   }
               }else{
                   Resource.Error(message = result.message(),data = null)
               }
               emit(handleResponse(result))*/
    }


    suspend fun getReferralAsDoctorAppStyle() : Flow<ApiEvent<List<com.example.feature_home.data.domain.RefferalList>>> = flow {
        runCatching {
            val apiResult = apiExecutor.callApi("/api/v1/allRef") {
                apiService.getAllRefferalAsDoctor()
            }
           // val apiResult = ApiClient.referralListMoshi.getAllRefferalAsDoctor()

            val apiEvent : ApiEvent<List<com.example.feature_home.data.domain.RefferalList>> = when(apiResult){
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response.map {it.toDomainMoshi()}){

                    replaceAllReferral(apiResult.response.map { it.toEntityMoshi()})
                    ApiEvent.OnSuccess.fromServer(this)
                }
            }
            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<List<com.example.feature_home.data.domain.RefferalList>>())
            Log.d("hal","Halo dunia")
        }
    }

    private fun handleResponse(response: Response<List<RefferalListResponse>>) : Resource<List<com.example.feature_home.data.domain.RefferalList>> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse.map { it.toDomain() })
            }
        }
        return Resource.Error(response.message())
    }

    suspend fun createPatient(request: RefferalListRequest) {
        val result = ApiClient.getReferralListApiGson.createPatient(request)
        val isCodeSuccess = Response.success(result).code()

        if (isCodeSuccess < 205){
            val hasil = result.body()!!.toEntity()
            insertReferral(hasil)
        }
    }
}


