package com.infomatica.inforestapp.domain


import com.infomatica.inforestapp.data.ProductoRepository
import com.infomatica.inforestapp.data.model.ProductoModel
import javax.inject.Inject

class ListaProductoUseCase @Inject constructor(
    private val repository: ProductoRepository
) {
    suspend operator fun invoke(canalventa: String, subgrupo: String): List<ProductoModel>? = repository.getAllProductos(canalventa, subgrupo)
}