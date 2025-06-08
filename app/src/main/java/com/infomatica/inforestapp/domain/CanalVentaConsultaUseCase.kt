package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.CanalVentaRepository
import com.infomatica.inforestapp.data.model.CanalVentaModel
import javax.inject.Inject

class CanalVentaConsultaUseCase @Inject constructor(
    private val repository: CanalVentaRepository
) {
    suspend operator fun invoke(): List<CanalVentaModel>? = repository.getAllCanalVentas()
}