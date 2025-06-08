package com.infomatica.inforestapp.data.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParametrosProvider @Inject constructor() {
    var parametro: ParametrosModel? = null
}
