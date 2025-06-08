package com.infomatica.inforestapp.data

import com.infomatica.inforestapp.data.model.ModificadorModel
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.data.model.ProductoProvider
import com.infomatica.inforestapp.data.network.ProductoService
import javax.inject.Inject

class ProductoRepository @Inject constructor(
    private val service: ProductoService,
    private  val provider: ProductoProvider
) {
    suspend fun getAllProductos(canalventa: String, subgrupo: String):List<ProductoModel>?{
        val response = service.getProductos(canalventa, subgrupo)
        provider.productos = response
        return response
    }
    suspend fun getModificadores(codigo: String, codigopedido:String, item:String):List<ModificadorModel>{
        val response = service.getModificadores(codigo, codigopedido, item)
//        if (response.status == 0) {
//            pedidoProvider.pedido = response
//        }
        return response
    }
}