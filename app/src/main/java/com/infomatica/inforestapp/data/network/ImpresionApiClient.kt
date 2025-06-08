package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.GrupoModel
import com.infomatica.inforestapp.data.model.MensajeImpresionModel
import com.infomatica.inforestapp.data.model.PedidoModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface ImpresionApiClient {

    @POST("Imprimir/precuenta")
    suspend fun  postPrecuenta(
        @Header("Authorization") token: String,  // Token Bearer
        @Body pedido: PedidoModel
    ): Response<PedidoModel?>

    @POST("Imprimir/comanda")
    suspend fun  postComanda(
        @Header("Authorization") token: String,  // Token Bearer
        @Body pedido: PedidoModel
    ): Response<PedidoModel?>

    @POST("Imprimir/liquidacion/{caja}")
    suspend fun  postLiquidacion(
        @Header("Authorization") token: String,  // Token Bearer
        @Path("caja") caja: String
    ): Response<ResponseBody>

    @POST("Imprimir/mensajeusuario")
    suspend fun  postMensajeImpresion(
        @Header("Authorization") token: String,  // Token Bearer
        @Body mensaje: MensajeImpresionModel
    ): Response<MensajeImpresionModel?>
}