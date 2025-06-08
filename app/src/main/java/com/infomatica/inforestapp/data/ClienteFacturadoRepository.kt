package com.infomatica.inforestapp.data

import com.infomatica.inforestapp.data.model.ClienteFacturadoModel
import com.infomatica.inforestapp.data.model.ClienteFacturadoProvider
import com.infomatica.inforestapp.data.network.ClienteFacturadoService
import javax.inject.Inject

class ClienteFacturadoRepository @Inject constructor(
    private val service: ClienteFacturadoService,
    private val provider: ClienteFacturadoProvider
) {
    suspend fun getCliente(codigo: String): ClienteFacturadoModel {
        val response = service.getCliente(codigo) ?: ClienteFacturadoModel(codigo = codigo)
        provider.clientefacturado = response
        return response
    }

    suspend fun getClienteRespository(): ClienteFacturadoModel {
        return provider.clientefacturado?:ClienteFacturadoModel(codigo = "")
    }

    suspend fun posClienteRespository(cliente: ClienteFacturadoModel): ClienteFacturadoModel {
        provider.clientefacturado = cliente
        return provider.clientefacturado?:ClienteFacturadoModel(codigo = "")
    }

}