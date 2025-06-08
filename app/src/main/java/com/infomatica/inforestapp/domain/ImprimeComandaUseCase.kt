package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ImpresionRepository
import com.infomatica.inforestapp.data.model.GrupoModel
import com.infomatica.inforestapp.data.model.PedidoModel
import javax.inject.Inject

class ImprimeComandaUseCase @Inject constructor(
    private val repository: ImpresionRepository
){
    suspend operator fun invoke(pedidoModel: PedidoModel, token: String): PedidoModel? = repository.postComanda(pedidoModel,token)
}