package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.data.model.ProductoProvider
import javax.inject.Inject

class ProductoProviderConsultaUseCase @Inject constructor(
    private val productoProvider: ProductoProvider
) {
    operator fun invoke(): ProductoModel? {
        val productoModel = productoProvider.producto
        productoModel?.let {
            return productoModel
        }
        return null
    }
}