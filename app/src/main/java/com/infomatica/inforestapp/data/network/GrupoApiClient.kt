package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.GrupoModel
import retrofit2.Response
import retrofit2.http.GET

interface GrupoApiClient {
    @GET("producto/grupo")
    suspend fun  getAllGrupos(): Response<List<GrupoModel>?>
}