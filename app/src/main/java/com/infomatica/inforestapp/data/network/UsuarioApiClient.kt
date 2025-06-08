package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.LoginModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.UsuarioModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UsuarioApiClient {
    @POST("Auth")
    suspend fun  postAuth(
        @Body login: LoginModel
    ): Response<UsuarioModel?>

    @POST("Auth/cerrarsesion")
    suspend fun  postCerrarSesion(
        @Header("Authorization") token: String,  // Token Bearer
    ): Response<UsuarioModel?>
}