package com.example.feature_home.ui_home

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.feature_home.data.CoreDatabase
import com.example.feature_home.data.api.ApiEvent
import com.example.feature_home.data.api.ApiExecutor
import com.example.feature_home.data.api.Resource
import com.example.feature_home.data.api.request.RefferalListRequest
import com.example.feature_home.data.api.response.RefferalListResponse
import com.example.feature_home.data.api.response.toDomain
import com.example.feature_home.data.domain.RefferalList
import com.example.feature_home.data.repository.RefferalRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.Response


internal class HomeViewModel
(private val refferalRepository: RefferalRepository) : ViewModel(){


    private val _readAllDataAsFlow = MutableLiveData<List<RefferalList>>()
    val readAllDataAsFlow : LiveData<List<RefferalList>> = _readAllDataAsFlow

    private val _readAllDataAsFlowState = MutableLiveData<Resource<List<RefferalList>>>()
    val readAllDataAsFlowState : LiveData<Resource<List<RefferalList>>> = _readAllDataAsFlowState

    private val _readAllDataAsFlowDoctor = MutableLiveData<ApiEvent<List<RefferalList>>>()
    val readAllDataAsFlowDoctor : LiveData<ApiEvent<List<RefferalList>>> = _readAllDataAsFlowDoctor

    var refResponse : MutableLiveData<Response<List<RefferalList>>> = MutableLiveData()

    init {
        checkFilterOrSort()
    }

    fun getDataReferral () {
        viewModelScope.launch {

            val response : Response<List<RefferalList>> = refferalRepository.getAllListReferral()
            _readAllDataAsFlow.value = response.body()
        }
    }

    fun addDataPatient (ref: com.example.feature_home.data.persistance.entity.RefferalList) {
        viewModelScope.launch {
            refferalRepository.insertReferral(ref)
        }
    }

    fun createPatient (request: RefferalListRequest) {
        viewModelScope.launch {
            refferalRepository.createPatient(request)
        }
    }

    private fun checkFilterOrSort (sortPatientName: Boolean = false,filter: Boolean= false, status: Int = 1) {
        viewModelScope.launch {
            val dataAsFlow = refferalRepository.getAllReferralAsFlow()

            if(sortPatientName){
                dataAsFlow.map { it.sortedBy { sort -> sort.patient_name } }.collect{_readAllDataAsFlow.value = it}
               // Log.d("urut","Masuk")
            }else if(filter){
                dataAsFlow.map { it.filter { params -> params.status == status } }.collect{_readAllDataAsFlow.value = it}
                Log.d("hai","sasa")
            }else{
                dataAsFlow.collect { _readAllDataAsFlow.value = it}
            }
            //dataAsFlow.map { it.filter { params -> equals(params.status == status) } }.collect{_readAllDataAsFlow.value = it}
        }
    }

    fun setFilterOrSort (sortPatientName: Boolean,filter: Boolean, status : Int = 1 ) {
        checkFilterOrSort(sortPatientName, filter, status)
    }


    fun getDataReferralAsState () {
        viewModelScope.launch {
            _readAllDataAsFlowState.postValue(Resource.Loading())
            val response = refferalRepository.getAllListReferralAsState()
            delay(5000)
            response.collect{ _readAllDataAsFlowState.value = it}
          //  _readAllDataAsFlowState.postValue(response)
        }
    }

    fun getDataReferralAsApiEvent () {
        viewModelScope.launch {
            delay(5000)
            refferalRepository.getReferralAsDoctorAppStyle()
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect{_readAllDataAsFlowDoctor.value = it}

        }
    }


    private fun handleResponse(response: Response<List<RefferalListResponse>>) : Resource<List<RefferalList>> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse.map { it.toDomain() })
            }
        }
        return Resource.Error(response.message())
    }

}