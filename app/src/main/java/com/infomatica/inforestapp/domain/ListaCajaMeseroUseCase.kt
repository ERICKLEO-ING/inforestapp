package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ImpresionRepository
import com.infomatica.inforestapp.data.ParametroRepository
import com.infomatica.inforestapp.data.model.ImpresoraCajaModel
import com.infomatica.inforestapp.data.model.ParametrosProvider
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.network.ParametroService
import javax.inject.Inject

class ListaCajaMeseroUseCase  @Inject constructor(
    private val repository: ParametroRepository
){
    suspend operator fun invoke(): List<ImpresoraCajaModel>? = repository.getCajaMesero()
}