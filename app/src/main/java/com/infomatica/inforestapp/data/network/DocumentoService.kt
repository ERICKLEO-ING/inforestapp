package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.PrepagoModel
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class DocumentoService @Inject constructor(
    private val api: DocumentoApiClient
) {
    suspend fun postDocumento(pedido: PedidoModel, token: String, ancho: Int): PedidoModel {
        return try {
            val response = api.postDocumento("Bearer $token", pedido,ancho)
            if (response.isSuccessful) {
                response.body() ?: PedidoModel(99, "Error: Body is null", "", "", "")
            } else {
                handleErrorResponse(response)
            }
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            PedidoModel(99, e.message ?: "Error desconocido", "", "", "")
        }
    }
    suspend fun postPrepago(prepago: PrepagoModel, token: String ): List<PrepagoModel> {
        return try {
            val response = api.postPrepago("Bearer $token", prepago)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: HttpException) {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deletePrepago(prepago: PrepagoModel, token: String ): List<PrepagoModel> {
        return try {
            val response = api.deletePrepago("Bearer $token", prepago)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: HttpException) {
            emptyList()
        } catch (e: Exception) {
            println(e.message)
            emptyList()
        }
    }
    private fun handleErrorResponse(response: Response<PedidoModel?>): PedidoModel {
        val errorBody = response.errorBody()?.string() ?: "Sin contenido de error"
        return when (response.code()) {
            400 -> PedidoModel(99, "Error 400: Bad Request - ${response.message()} - Contenido: $errorBody", "", "", "")
            401 -> PedidoModel(99, "Error 401: Unauthorized - ${response.message()} - Contenido: $errorBody", "", "", "")
            403 -> PedidoModel(99, "Error 403: Forbidden - ${response.message()} - Contenido: $errorBody", "", "", "")
            404 -> PedidoModel(99, "Error 404: Not Found - ${response.message()} - Contenido: $errorBody", "", "", "")
            else -> PedidoModel(99, "Error ${response.code()}: ${response.message()} - Contenido: $errorBody", "", "", "")
        }
    }

    private fun handleHttpException(e: HttpException): PedidoModel {
        return when (e.code()) {
            400 -> PedidoModel(99, "Error 400: Bad Request - ${e.message()}", "", "", "")
            else -> PedidoModel(99, "Error: ${e.message()}", "", "", "")
        }
    }
}
