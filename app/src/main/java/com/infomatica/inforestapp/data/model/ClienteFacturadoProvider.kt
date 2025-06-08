package com.infomatica.inforestapp.data.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClienteFacturadoProvider @Inject constructor() {
    var clientefacturado: ClienteFacturadoModel? = null
}