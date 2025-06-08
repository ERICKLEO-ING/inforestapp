package com.infomatica.inforestapp.data

import com.infomatica.inforestapp.data.model.ModificadorModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.PedidoProvider
import com.infomatica.inforestapp.data.model.UsuarioProvider
import com.infomatica.inforestapp.data.network.PedidoService
import com.infomatica.inforestapp.domain.UsuarioConsultaUseCase
import javax.inject.Inject

class PedidoRepository @Inject constructor(
    private val service: PedidoService,
    private  val provider: PedidoProvider,
    private  val usuarioConsultaUseCase: UsuarioConsultaUseCase
) {
    suspend fun postPedido(pedido: PedidoModel):PedidoModel{
        val usuario =usuarioConsultaUseCase()
        val response = service.postPedido(pedido, usuario!!.token)
        if (response.status == 0) {
            provider.pedido = response
        }
        return response
    }
    suspend fun postPedidoTransferir(pedido: PedidoModel, token: String):PedidoModel{
        val response = service.postPedidoTransferir(pedido, token)
        if (response.status == 0) {
            provider.pedido = response
        }
        return response
    }
    suspend fun postDeletePedido(pedido: PedidoModel, token: String, passSupervisor: String):PedidoModel?{
        val response = service.postDeletePedido(pedido, token,passSupervisor)
        if (response.status == 0) {
            provider.pedido = response
        }
        return response
    }
    suspend fun postPedidoDetalle(pedido: PedidoModel, token: String):PedidoModel{
        val response = service.postPedidoDetalle(pedido, token)
        if (response.status == 0) {
            provider.pedido = response
        }
        return response
    }
    suspend fun deletePedidoDetalle(pedido: PedidoModel, token: String, passSupervisor: String):PedidoModel?{
        val response = service.deletePedidoDetalle(pedido, token,passSupervisor)
        if (response.status == 0) {
            provider.pedido = response
        }
        return response
    }
    suspend fun getPedidoById(id: String):PedidoModel?{

        val usuario =usuarioConsultaUseCase()
        val response = usuario?.token?.let { service.getPedidoById(id, "Bearer " +it) }
        if (response?.status == 0) {
            provider.pedido = response
        }
        return response
    }
}