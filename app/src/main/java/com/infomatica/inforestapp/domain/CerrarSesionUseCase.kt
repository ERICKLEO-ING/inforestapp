package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.UsuarioRepository
import com.infomatica.inforestapp.data.model.LoginModel
import com.infomatica.inforestapp.data.model.UsuarioModel
import javax.inject.Inject


class CerrarSesionUseCase @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(): UsuarioModel? = repository.postCerrarSesion()
}