package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.ResponseModel
import com.infomatica.inforestapp.data.model.TurnoModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface TurnoApiClient {

    @GET("turno/{caja}")
    suspend fun getTurno(
        @Header("Authorization") token: String,  // Token Bearer
        @Path("caja") caja: String
    ): Response<List<TurnoModel>?>

    @POST("turno/{caja}")
    suspend fun postTurno(
        @Header("Authorization") token: String,  // Token Bearer
        @Path("caja") caja: String
    ): Response<List<TurnoModel>?>

    @POST("turno/marcacion/{password}")
    suspend fun postMarcacion(
        @Path("password") password: String
    ): Response<List<ResponseModel>?>
}