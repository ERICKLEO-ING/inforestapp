package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.ModificadorModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.ProductoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class ProductoService @Inject constructor(
    private val api:ProductoApiClient
){
    suspend fun  getProductos(canalventa: String, subgrupo: String): List<ProductoModel>{
        return withContext(Dispatchers.IO){
            val response = api.getAllProductos(canalventa, subgrupo)
            response.body()?: emptyList()
        }
    }
    suspend fun  getModificadores(codigo: String, codigopedido: String, item: String): List<ModificadorModel>{
        return withContext(Dispatchers.IO){
            val response = api.getModificadores(codigo, codigopedido, item)
            response.body()?: emptyList()
        }
    }

//    suspend fun  getModificadores(codigo: String): List<ModificadorModel>{
//        return withContext(Dispatchers.IO){
//            val response = api.getModificadores(codigo)
//            response.body()?: emptyList()
//        }
//    }

//    suspend fun getModificadores(codigo: String): List<ModificadorModel> {
//        return try  {
//            val response = api.getModificadores(codigo)
//            if (response.isSuccessful) {
//                response.body() ?: ModificadorModel(99, "Error: Body is null", "", "", "")
//            } else {
//                handleErrorResponse(response)
//            }
//        } catch (e: HttpException) {
//            handleHttpException(e)
//        } catch (e: Exception) {
//            ModificadorModel(99, e.message ?: "Error desconocido","","")
//        }
//    }

    private fun handleErrorResponse(response: Response<ModificadorModel?>): ModificadorModel {
        val errorBody = response.errorBody()?.string() ?: "Sin contenido de error"
        return when (response.code()) {
            400 -> ModificadorModel(99, "Error 400: Bad Request - ${response.message()} - Contenido: $errorBody", "", "", "")
            401 -> ModificadorModel(99, "Error 401: Unauthorized - ${response.message()} - Contenido: $errorBody", "", "", "")
            403 -> ModificadorModel(99, "Error 403: Forbidden - ${response.message()} - Contenido: $errorBody", "", "", "")
            404 -> ModificadorModel(99, "Error 404: Not Found - ${response.message()} - Contenido: $errorBody", "", "", "")
            else -> ModificadorModel(99, "Error ${response.code()}: ${response.message()} - Contenido: $errorBody", "", "", "")
        }
    }

    private fun handleHttpException(e: HttpException): ModificadorModel {
        return when (e.code()) {
            400 -> ModificadorModel(99, "Error 400: Bad Request - ${e.message()}", "", "", "")
            else -> ModificadorModel(99, "Error: ${e.message()}", "", "", "")
        }
    }
}