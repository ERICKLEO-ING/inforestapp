package com.infomatica.inforestapp.data

import com.infomatica.inforestapp.data.model.GrupoModel
import com.infomatica.inforestapp.data.model.GrupoProvider
import com.infomatica.inforestapp.data.network.GrupoService
import javax.inject.Inject

class GrupoRepository @Inject constructor(
    private val service: GrupoService,
    private  val provider: GrupoProvider
) {
    suspend fun getAllGrupos():List<GrupoModel>?{
        val response = service.getGrupo()
        provider.grupos = response
        return response
    }
}