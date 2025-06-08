package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.PedidoRepository
import com.infomatica.inforestapp.data.model.PedidoModel
import javax.inject.Inject

class PedidoConsultaApiUseCase @Inject constructor(
    private val repository: PedidoRepository
) {
    suspend operator fun invoke(id: String): PedidoModel? = repository.getPedidoById(id)
}