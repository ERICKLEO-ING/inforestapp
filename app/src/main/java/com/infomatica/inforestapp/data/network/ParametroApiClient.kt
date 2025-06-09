package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.AlertasModel
import com.infomatica.inforestapp.data.model.ImpresoraCajaModel
import com.infomatica.inforestapp.data.model.MensajeMeseroModel
import com.infomatica.inforestapp.data.model.MotivoDescuentoModel
import com.infomatica.inforestapp.data.model.MotivoEliminacionModel
import com.infomatica.inforestapp.data.model.OfertaModel
import com.infomatica.inforestapp.data.model.ParametrosModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.SalonModel
import com.infomatica.inforestapp.data.model.TarjetaBancariaModel
import com.infomatica.inforestapp.data.model.TipoIdentidadModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ParametroApiClient {

    @GET("parametro")
    suspend fun getParametros(): Response<List<ParametrosModel>?>

    @GET("parametro/motivoeliminacion")
    suspend fun getMotivoEliminacion(): Response<List<MotivoEliminacionModel>?>

    @GET("parametro/motivodescuento")
    suspend fun getMotivoDescuento(): Response<List<MotivoDescuentoModel>?>

    @GET("parametro/mensaje")
    suspend fun getMensaje(): Response<List<AlertasModel>?>

    @GET("parametro/oferta")
    suspend fun getOferta(): Response<List<OfertaModel>?>

    @GET("parametro/tarjetabancaria")
    suspend fun getTarjetaBancaria(): Response<List<TarjetaBancariaModel>?>

    @GET("parametro/mensajemesero")
    suspend fun getMensajeMesero(): Response<List<MensajeMeseroModel>?>

    @GET("parametro/impresoracaja/{caja}")
    suspend fun getImpresoraCaja(
        @Path("caja") caja: String
    ): Response<List<ImpresoraCajaModel>?>

    @GET("parametro/tipoidentidad")
    suspend fun getTipoIdentidad(): Response<List<TipoIdentidadModel>?>

    @GET("parametro/cajamesero")
    suspend fun getCajaMesero(): Response<List<ImpresoraCajaModel>?>
}