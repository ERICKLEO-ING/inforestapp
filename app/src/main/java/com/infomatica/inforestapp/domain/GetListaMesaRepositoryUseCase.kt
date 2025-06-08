package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.model.SalonModel
import com.infomatica.inforestapp.data.model.SalonProvider
import javax.inject.Inject


class GetListaMesaRepositoryUseCase @Inject constructor(
    private val provider: SalonProvider
) {
    operator fun invoke(): List<SalonModel>? {
        val salones = provider.salones
        return salones
    }
}
