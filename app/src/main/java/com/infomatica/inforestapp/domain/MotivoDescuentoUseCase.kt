package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ParametroRepository
import com.infomatica.inforestapp.data.model.MotivoDescuentoModel
import javax.inject.Inject

class MotivoDescuentoUseCase @Inject constructor(private val  repository: ParametroRepository) {
    suspend operator fun invoke(): List<MotivoDescuentoModel> = repository.getMotivoDescuento()
}
