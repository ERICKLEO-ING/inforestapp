package com.infomatica.inforestapp.data.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalonProvider @Inject constructor() {
    var salones: List<SalonModel>? = emptyList()
}