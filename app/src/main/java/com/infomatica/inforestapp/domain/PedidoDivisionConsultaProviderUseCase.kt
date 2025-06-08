package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.PedidoProvider
import javax.inject.Inject

class PedidoDivisionConsultaProviderUseCase @Inject constructor(
    private val pedidoProvider: PedidoProvider
) {
    operator fun invoke(): PedidoModel? {
        val pedido = pedidoProvider.pedidoDivision
        pedido?.let {
            return pedido
        }
        return null
    }
}