package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ParametroRepository
import com.infomatica.inforestapp.data.model.ParametrosModel
import javax.inject.Inject

class ParametroUseCase @Inject constructor(private val  repository: ParametroRepository) {
    suspend operator fun invoke(): ParametrosModel? = repository.getParametros()
}