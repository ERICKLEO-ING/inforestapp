package com.infomatica.inforestapp.data.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrupoProvider @Inject constructor() {
    var grupos: List<GrupoModel>? = emptyList()
}