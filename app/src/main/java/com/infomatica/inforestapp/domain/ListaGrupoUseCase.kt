package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.GrupoRepository
import com.infomatica.inforestapp.data.model.GrupoModel
import javax.inject.Inject


class ListaGrupoUseCase @Inject constructor(
    private val repository: GrupoRepository
) {
    suspend operator fun invoke(): List<GrupoModel>? = repository.getAllGrupos()
}
