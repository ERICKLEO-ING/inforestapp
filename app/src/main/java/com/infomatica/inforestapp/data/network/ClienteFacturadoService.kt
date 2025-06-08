package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.ClienteFacturadoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClienteFacturadoService @Inject constructor(
    private val api: ClienteFacturadoApiClient
) {
    suspend fun getCliente(codigo: String): ClienteFacturadoModel {
        return withContext(Dispatchers.IO) {
            val response = api.getCliente(codigo)
            response.body() ?: ClienteFacturadoModel(codigo = codigo)
        }
    }
}
