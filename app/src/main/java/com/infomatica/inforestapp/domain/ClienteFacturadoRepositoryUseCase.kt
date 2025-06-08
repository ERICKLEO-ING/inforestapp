package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ClienteFacturadoRepository
import com.infomatica.inforestapp.data.model.ClienteFacturadoModel
import javax.inject.Inject

class ClienteFacturadoRepositoryUseCase @Inject constructor(
    private val repository: ClienteFacturadoRepository
) {
    suspend operator fun invoke(): ClienteFacturadoModel? = repository.getClienteRespository()
}
