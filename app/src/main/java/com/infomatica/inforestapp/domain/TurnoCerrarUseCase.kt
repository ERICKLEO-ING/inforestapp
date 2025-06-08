package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.TurnoRepository
import com.infomatica.inforestapp.data.model.TurnoModel
import javax.inject.Inject


class TurnoCerrarUseCase  @Inject constructor(
    private val repository: TurnoRepository
) {
    suspend operator fun invoke(caja: String): TurnoModel {
        val respuesta = repository.postTurno(caja)
        return respuesta
    }
}