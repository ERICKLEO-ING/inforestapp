package com.infomatica.inforestapp.data

import com.infomatica.inforestapp.data.model.LoginModel
import com.infomatica.inforestapp.data.model.SalonModel
import com.infomatica.inforestapp.data.model.SalonProvider
import com.infomatica.inforestapp.data.model.UsuarioModel
import com.infomatica.inforestapp.data.model.UsuarioProvider
import com.infomatica.inforestapp.data.network.SalonService
import com.infomatica.inforestapp.data.network.UsuarioService
import com.infomatica.inforestapp.domain.UsuarioConsultaUseCase
import javax.inject.Inject


class UsuarioRepository @Inject constructor(
    private val service: UsuarioService,
    private  val provider: UsuarioProvider,
    private  val usuarioConsultaUseCase: UsuarioConsultaUseCase
) {
    suspend fun postUsuario(login: LoginModel): UsuarioModel?{
        val response = service.postUsuario(login)
        provider.usuario = response
        return response
    }

    suspend fun postCerrarSesion(): UsuarioModel?{
        val usuario =usuarioConsultaUseCase()
        val response = service.postCerrarSesion(usuario!!.token)
        provider.usuario = null
        return response
    }
}