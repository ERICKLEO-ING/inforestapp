package com.infomatica.inforestapp.data.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioProvider @Inject constructor() {
    var usuario: UsuarioModel? = null
}
