package com.infomatica.inforestapp.data.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductoProvider @Inject constructor() {
    var productos: List<ProductoModel> = emptyList() // Inicialización con lista vacía
    var producto: ProductoModel? = null // Inicialización como null
}