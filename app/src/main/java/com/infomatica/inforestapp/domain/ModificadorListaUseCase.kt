package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ProductoRepository
import com.infomatica.inforestapp.data.model.ModificadorModel
import com.infomatica.inforestapp.data.model.QuoteModel
import javax.inject.Inject

class ModificadorListaUseCase @Inject constructor( private val  repository: ProductoRepository) {
    suspend operator fun invoke(codigo: String, codigopedido:String, item:String): List<ModificadorModel> = repository.getModificadores(codigo, codigopedido, item)
}