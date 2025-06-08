package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.GrupoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GrupoService @Inject constructor(
    private val api:GrupoApiClient
){
    suspend fun  getGrupo(): List<GrupoModel>?{
        return withContext(Dispatchers.IO){
            val response = api.getAllGrupos()
            response.body()?: emptyList()
        }
    }
}