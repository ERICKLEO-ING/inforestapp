package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.PedidoProvider
import javax.inject.Inject

class PedidoDivisionGuardarProviderUseCase @Inject constructor(
    private val pedidoProvider: PedidoProvider
) {
    operator fun invoke(pedido: PedidoModel): PedidoModel? {
        pedidoProvider.pedidoDivision = pedido
        pedidoProvider.pedidoDivision?.let {
            return pedido
        }
        return null
    }
}