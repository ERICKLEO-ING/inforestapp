package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.SalonModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SalonService @Inject constructor(
    private val api:SalonApiClient
){
    suspend fun  getSalones(): List<SalonModel>?{
        return withContext(Dispatchers.IO){
            val response = api.getAllSalones()
            response.body()?: emptyList()
        }
    }
}