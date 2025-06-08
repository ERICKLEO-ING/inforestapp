package com.infomatica.inforestapp.data.model

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PedidoProvider @Inject constructor() {
    var pedido: PedidoModel? = null
    var pedidoDivision: PedidoModel? = null
}