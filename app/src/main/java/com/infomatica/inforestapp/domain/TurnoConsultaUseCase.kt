package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.TurnoRepository
import com.infomatica.inforestapp.data.model.TurnoModel
import javax.inject.Inject

class TurnoConsultaUseCase  @Inject constructor(
    private val repository: TurnoRepository
) {
    suspend operator fun invoke(caja: String): TurnoModel {
        val respuesta = repository.getTurno(caja)
        return respuesta
    }
}