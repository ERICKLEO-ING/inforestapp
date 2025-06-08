package com.infomatica.inforestapp.data

import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.PedidoProvider
import com.infomatica.inforestapp.data.model.PrepagoModel
import com.infomatica.inforestapp.data.network.DocumentoService
import com.infomatica.inforestapp.domain.UsuarioConsultaUseCase
import javax.inject.Inject


class DocumentoRepository @Inject constructor(
    private val service: DocumentoService,
    private  val provider: PedidoProvider,
    private  val usuarioConsultaUseCase: UsuarioConsultaUseCase
) {
    suspend fun postDocumento(pedido: PedidoModel, ancho: Int): PedidoModel? {
        val usuario =usuarioConsultaUseCase()
        val response = usuario?.let { service.postDocumento(pedido, it.token, ancho) }
        if (response != null) {
            if (response.status == 0) {
                provider.pedido = response
            }
        }
        return response
    }
    suspend fun postPrepago(prepago: PrepagoModel): List<PrepagoModel> {
        val usuario =usuarioConsultaUseCase()
        val response = usuario?.let { service.postPrepago(prepago, it.token) }
        return response!!
    }

    suspend fun deletePrepago(prepago: PrepagoModel): List<PrepagoModel> {
        val usuario =usuarioConsultaUseCase()
        val response = usuario?.let { service.deletePrepago(prepago, it.token) }
        return response!!
    }
}