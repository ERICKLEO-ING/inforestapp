package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ParametroRepository
import com.infomatica.inforestapp.data.model.MensajeMeseroModel
import javax.inject.Inject


class MensajeMeseroConsultaUseCase @Inject constructor(private val  repository: ParametroRepository) {
    suspend operator fun invoke(): List<MensajeMeseroModel> = repository.getMensajeMesero()
}