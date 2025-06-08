package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.DocumentoRepository
import com.infomatica.inforestapp.data.PedidoRepository
import com.infomatica.inforestapp.data.model.PedidoModel
import javax.inject.Inject


class DocumentoProcesaUseCase @Inject constructor(
    private val repository: DocumentoRepository
) {
    suspend operator fun invoke(pedidoModel: PedidoModel, ancho: Int): PedidoModel? = repository.postDocumento(pedidoModel,ancho)
}