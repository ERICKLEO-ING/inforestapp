package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.PrepagoModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface DocumentoApiClient {

    @POST("documento/{ancho}")
    suspend fun  postDocumento(
        @Header("Authorization") token: String,  // Token Bearer
        @Body pedido: PedidoModel,
        @Path("ancho") ancho: Int =32
    ): Response<PedidoModel?>

    @POST("documento/prepago")
    suspend fun  postPrepago(
        @Header("Authorization") token: String,  // Token Bearer
        @Body prepago: PrepagoModel
    ): Response<List<PrepagoModel>>

    @POST("documento/prepagodelete")
    suspend fun  deletePrepago(
        @Header("Authorization") token: String,  // Token Bearer
        @Body prepago: PrepagoModel
    ): Response<List<PrepagoModel>>
}