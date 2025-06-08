package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.SalonRepository
import com.infomatica.inforestapp.data.model.QuoteModel
import com.infomatica.inforestapp.data.model.SalonModel
import com.infomatica.inforestapp.data.model.SalonProvider
import javax.inject.Inject


class GetListaSalonUseCase @Inject constructor(
    private val repository: SalonRepository
) {
    suspend operator fun invoke(): List<SalonModel>? = repository.getAllSalones()
}
