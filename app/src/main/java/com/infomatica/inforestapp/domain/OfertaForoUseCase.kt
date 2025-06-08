package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ParametroRepository
import com.infomatica.inforestapp.data.model.AlertasModel
import com.infomatica.inforestapp.data.model.OfertaModel
import javax.inject.Inject


class OfertaForoUseCase @Inject constructor(private val  repository: ParametroRepository) {
    suspend operator fun invoke(): List<OfertaModel> = repository.getOferta()
}