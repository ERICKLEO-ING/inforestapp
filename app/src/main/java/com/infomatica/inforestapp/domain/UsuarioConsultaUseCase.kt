package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.model.QuoteModel
import com.infomatica.inforestapp.data.model.UsuarioModel
import com.infomatica.inforestapp.data.model.UsuarioProvider
import javax.inject.Inject

class UsuarioConsultaUseCase @Inject constructor(private val usuarioProvider: UsuarioProvider) {
    operator fun invoke(): UsuarioModel? {
        val usuario = usuarioProvider.usuario
        if (!usuario?.token.isNullOrEmpty()) {
            return usuario
        }
        return null
    }
}