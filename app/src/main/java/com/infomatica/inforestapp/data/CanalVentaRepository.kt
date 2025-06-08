package com.infomatica.inforestapp.data

import com.infomatica.inforestapp.data.model.CanalVentaModel
import com.infomatica.inforestapp.data.model.CanalVentaProvider
import com.infomatica.inforestapp.data.model.GrupoModel
import com.infomatica.inforestapp.data.model.GrupoProvider
import com.infomatica.inforestapp.data.network.CanalVentaService
import com.infomatica.inforestapp.data.network.GrupoService
import javax.inject.Inject

class CanalVentaRepository @Inject constructor(
    private val service: CanalVentaService,
    private  val provider: CanalVentaProvider
) {
    suspend fun getAllCanalVentas():List<CanalVentaModel>?{
        val response = service.getCanalVentas()
        provider.canalventas = response
        return response
    }
}