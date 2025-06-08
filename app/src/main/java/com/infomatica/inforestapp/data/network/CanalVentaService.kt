package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.CanalVentaModel
import com.infomatica.inforestapp.data.model.GrupoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CanalVentaService @Inject constructor(
    private val api:CanalVentaApiClient
){
    suspend fun  getCanalVentas(): List<CanalVentaModel>?{
        return withContext(Dispatchers.IO){
            val response = api.getAllCanalVentas()
            response.body()?: emptyList()
        }
    }
}