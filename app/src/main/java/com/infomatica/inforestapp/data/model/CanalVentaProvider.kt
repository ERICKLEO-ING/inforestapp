package com.infomatica.inforestapp.data.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CanalVentaProvider @Inject constructor() {
    var canalventas: List<CanalVentaModel>? = emptyList()
}