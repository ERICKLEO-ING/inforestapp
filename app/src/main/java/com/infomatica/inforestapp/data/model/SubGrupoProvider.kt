package com.infomatica.inforestapp.data.model

import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class SubGrupoProvider @Inject constructor() {
    var subgrupo: List<SubGrupoModel>? = emptyList()
}
