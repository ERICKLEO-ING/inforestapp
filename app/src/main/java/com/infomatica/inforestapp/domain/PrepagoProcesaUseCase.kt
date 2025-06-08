package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.DocumentoRepository
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.PrepagoModel
import javax.inject.Inject

class PrepagoProcesaUseCase @Inject constructor(
    private val repository: DocumentoRepository
) {
    suspend operator fun invoke(prepago: PrepagoModel): List<PrepagoModel> = repository.postPrepago(prepago)
}