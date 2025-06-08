package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.PedidoRepository
import com.infomatica.inforestapp.data.model.PedidoModel
import javax.inject.Inject


class PedidoTransferirUseCase @Inject constructor(
    private val repository: PedidoRepository
) {
    suspend operator fun invoke(pedidoModel: PedidoModel, token: String): PedidoModel = repository.postPedidoTransferir(pedidoModel,token)
}