package com.infomatica.inforestapp.data.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MotivoDescuentoProvider @Inject constructor() {
    var motivo: List<MotivoDescuentoModel> = emptyList()
}
