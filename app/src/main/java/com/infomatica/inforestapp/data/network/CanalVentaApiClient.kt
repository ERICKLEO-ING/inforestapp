package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.CanalVentaModel
import retrofit2.Response
import retrofit2.http.GET

interface CanalVentaApiClient {
    @GET("canalventa")
    suspend fun  getAllCanalVentas(): Response<List<CanalVentaModel>?>
}