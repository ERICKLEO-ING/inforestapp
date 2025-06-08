package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ParametroRepository
import com.infomatica.inforestapp.data.model.ImpresoraCajaModel
import com.infomatica.inforestapp.data.model.MensajeMeseroModel
import javax.inject.Inject


class ImpresoraCajaConsultaUseCase @Inject constructor(private val  repository: ParametroRepository) {
    suspend operator fun invoke(caja: String): List<ImpresoraCajaModel> = repository.getImpresoraCaja(caja)
}