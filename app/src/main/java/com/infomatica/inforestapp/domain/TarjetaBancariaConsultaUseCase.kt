package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ParametroRepository
import com.infomatica.inforestapp.data.model.OfertaModel
import com.infomatica.inforestapp.data.model.TarjetaBancariaModel
import javax.inject.Inject

class TarjetaBancariaConsultaUseCase @Inject constructor(private val  repository: ParametroRepository) {
    suspend operator fun invoke(): List<TarjetaBancariaModel> = repository.getTarjetaBancaria()
}