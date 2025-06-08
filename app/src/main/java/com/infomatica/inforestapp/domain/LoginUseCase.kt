package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ProductoRepository
import com.infomatica.inforestapp.data.UsuarioRepository
import com.infomatica.inforestapp.data.model.LoginModel
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.data.model.UsuarioModel
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(login: LoginModel): UsuarioModel? = repository.postUsuario(login)
}