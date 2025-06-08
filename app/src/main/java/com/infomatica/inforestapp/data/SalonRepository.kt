package com.infomatica.inforestapp.data

import com.infomatica.inforestapp.data.model.SalonModel
import com.infomatica.inforestapp.data.model.SalonProvider
import com.infomatica.inforestapp.data.network.SalonService
import javax.inject.Inject

class SalonRepository @Inject constructor(
    private val service: SalonService,
    private  val provider: SalonProvider
) {
    suspend fun getAllSalones():List<SalonModel>?{
        val response = service.getSalones()
        provider.salones = response
        return response
    }
}