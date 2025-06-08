package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ImpresionRepository
import com.infomatica.inforestapp.data.model.PedidoModel
import javax.inject.Inject

class ImprimeLiquidacionUseCase @Inject constructor(
    private val repository: ImpresionRepository
){
    suspend operator fun invoke(caja: String): String = repository.postLiquidacion(caja)
}