package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.LoginModel
import com.infomatica.inforestapp.data.model.UsuarioModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UsuarioService @Inject constructor(
    private val api:UsuarioApiClient
){
    suspend fun  postUsuario(login: LoginModel): UsuarioModel?{
        return withContext(Dispatchers.IO){
            val response = api.postAuth(login)
            response.body()
        }
    }
    suspend fun  postCerrarSesion(token: String): UsuarioModel?{
        return withContext(Dispatchers.IO){
            val response = api.postCerrarSesion("Bearer $token")
            response.body()
        }
    }
}