package com.infomatica.inforestapp.data

import com.infomatica.inforestapp.data.model.MensajeImpresionModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.PedidoProvider
import com.infomatica.inforestapp.data.network.ImpresionService
import com.infomatica.inforestapp.domain.UsuarioConsultaUseCase
import javax.inject.Inject

class ImpresionRepository @Inject constructor(
    private val service: ImpresionService,
    private  val provider: PedidoProvider,
    private  val usuarioConsultaUseCase: UsuarioConsultaUseCase
) {
    suspend fun postPrecuenta(pedido: PedidoModel, token: String): PedidoModel{
        val response = service.postPrecuenta(pedido, token)
        return response
    }
    suspend fun postComanda(pedido: PedidoModel, token: String): PedidoModel{
        val response = service.postComanda(pedido, token)
        if (response.status == 0) {
            provider.pedido = response
        }
        return response
    }
    suspend fun postLiquidacion(caja: String): String {
        return try {
            val usuario = usuarioConsultaUseCase()
            if (usuario == null) {
                "Error: Usuario no encontrado"
            } else {
                val response = service.postLiquidacion(caja,usuario.token)
                response ?: "Error: Respuesta vacía"
            }
        } catch (e: Exception) {
            "Error inesperado: ${e.localizedMessage}"
        }
    }
    suspend fun postMensajeImpresion(mensaje: MensajeImpresionModel): MensajeImpresionModel {
        val usuario =usuarioConsultaUseCase()
        val response = service.postMensajeImpresion(mensaje, usuario!!.token)
        return response
    }
}