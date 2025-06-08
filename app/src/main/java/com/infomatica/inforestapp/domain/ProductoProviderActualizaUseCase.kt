package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.data.model.ProductoProvider
import javax.inject.Inject

class ProductoProviderActualizaUseCase @Inject constructor(
    private val productoProvider: ProductoProvider
) {
    operator fun invoke(nuevoProducto: ProductoModel): ProductoModel? {
        // Crear una nueva instancia (limpia)
        val copiaProducto = nuevoProducto.copy()
        productoProvider.producto = copiaProducto
        return copiaProducto
    }
}