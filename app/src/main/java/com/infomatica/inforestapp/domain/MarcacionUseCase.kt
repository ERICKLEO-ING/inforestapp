package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.TurnoRepository
import com.infomatica.inforestapp.data.model.ResponseModel
import com.infomatica.inforestapp.data.model.TurnoModel
import javax.inject.Inject

class MarcacionUseCase  @Inject constructor(
    private val repository: TurnoRepository
) {
    suspend operator fun invoke(pass: String): ResponseModel {
        val respuesta = repository.postMarcacion(pass)
        return respuesta
    }
}