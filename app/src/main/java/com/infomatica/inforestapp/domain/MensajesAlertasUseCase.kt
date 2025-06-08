package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ParametroRepository
import com.infomatica.inforestapp.data.model.AlertasModel
import com.infomatica.inforestapp.data.model.MotivoDescuentoModel
import javax.inject.Inject


class MensajesAlertasUseCase @Inject constructor(private val  repository: ParametroRepository) {
    suspend operator fun invoke(): List<AlertasModel> = repository.getMensaje()
}