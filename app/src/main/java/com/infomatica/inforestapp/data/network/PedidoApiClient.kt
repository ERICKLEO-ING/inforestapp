package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.PedidoModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface PedidoApiClient {

    @POST("pedido")
    suspend fun  postPedido(
        @Header("Authorization") token: String,  // Token Bearer
        @Body pedido: PedidoModel
    ): Response<PedidoModel?>

    @POST("pedido/transferir")
    suspend fun  postPedidoTransferir(
        @Header("Authorization") token: String,  // Token Bearer
        @Body pedido: PedidoModel
    ): Response<PedidoModel?>

    @POST("pedido/{password}")
    suspend fun  postDeletePedido(
        @Header("Authorization") token: String,  // Token Bearer
        @Body pedido: PedidoModel,
        @Path("password") password: String ="00"
    ): Response<PedidoModel?>

    @GET("pedido/{id}")
    suspend fun getPedidoById(
        @Header("Authorization") token: String,  // Token Bearer
        @Path("id") id: String
    ): Response<PedidoModel?>

    @POST("pedido/detalle")
    suspend fun  postPedidoDetalle(
        @Header("Authorization") token: String,  // Token Bearer
        @Body pedido: PedidoModel
    ): Response<PedidoModel?>

    @POST("pedido/detalle/{password}")
    suspend fun  deletePedidoDetalle(
        @Header("Authorization") token: String,  // Token Bearer
        @Body pedido: PedidoModel,
        @Path("password") password: String ="00"
    ): Response<PedidoModel?>
}