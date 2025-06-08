package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ParametroRepository
import com.infomatica.inforestapp.data.model.TipoIdentidadModel
import javax.inject.Inject

class TipoIdentidadConsultaUseCase @Inject constructor(private val  repository: ParametroRepository) {
    suspend operator fun invoke(): List<TipoIdentidadModel> = repository.getTipoIdentidad()
}