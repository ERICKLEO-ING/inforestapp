package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.ClienteFacturadoModel
import com.infomatica.inforestapp.data.model.ProductoModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface ClienteFacturadoApiClient {
    // Obtener todos consulta de cliente
    @GET("cliente/{codigo}")
    suspend fun getCliente(
        @Path("codigo") codigo: String = ""
    ): Response<ClienteFacturadoModel?>
}

