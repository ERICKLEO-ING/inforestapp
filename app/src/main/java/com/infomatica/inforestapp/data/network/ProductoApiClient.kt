package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.ModificadorModel
import com.infomatica.inforestapp.data.model.ProductoModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface ProductoApiClient {

    // Obtener todos los productos por canal de venta y subgrupo
    @GET("Producto/{canalventa}/{subgrupo}")
    suspend fun getAllProductos(
        @Path("canalventa") canalVenta: String,
        @Path("subgrupo") subGrupo: String
    ): Response<List<ProductoModel>?>

    // Obtener un producto específico por su ID
    @GET("Producto/{id}")
    suspend fun getProductoById(
        @Path("id") id: String
    ): Response<ProductoModel?>

    @GET("Producto/modificadores/{codigo}/{codigopedido}/{item}")
    suspend fun getModificadores(
        @Path("codigo") codigo: String,
        @Path("codigopedido") codigopedido: String,
        @Path("item") item: String
    ): Response<List<ModificadorModel>?>

//    // Buscar productos por nombre o descripción
//    @GET("Producto/search")
//    suspend fun searchProductos(
//        @Query("query") query: String
//    ): Response<List<ProductoModel>?>
//
//    // Agregar un nuevo producto
//    @POST("Producto")
//    suspend fun addProducto(
//        @Body producto: ProductoModel
//    ): Response<ProductoModel?>
//
//    // Actualizar un producto existente
//    @PUT("Producto/{id}")
//    suspend fun updateProducto(
//        @Path("id") id: String,
//        @Body producto: ProductoModel
//    ): Response<ProductoModel?>
//
//    // Eliminar un producto
//    @DELETE("Producto/{id}")
//    suspend fun deleteProducto(
//        @Path("id") id: String
//    ): Response<Void>
}
