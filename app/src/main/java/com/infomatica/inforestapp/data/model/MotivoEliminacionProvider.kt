package com.infomatica.inforestapp.data.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MotivoEliminacionProvider @Inject constructor() {
    var motivoEliminacion: List<MotivoEliminacionModel> = emptyList()
}
