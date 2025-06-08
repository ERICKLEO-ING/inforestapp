package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ClienteFacturadoRepository
import com.infomatica.inforestapp.data.model.ClienteFacturadoModel
import javax.inject.Inject

class ClienteFacturadoGuardarUseCase @Inject constructor(
    private val repository: ClienteFacturadoRepository
) {
    suspend operator fun invoke(cliente: ClienteFacturadoModel): ClienteFacturadoModel? = repository.posClienteRespository(cliente)
}
