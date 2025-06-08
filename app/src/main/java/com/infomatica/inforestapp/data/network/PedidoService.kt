package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.PedidoModel
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class PedidoService @Inject constructor(
    private val api: PedidoApiClient
) {
    suspend fun postPedido(pedido: PedidoModel, token: String): PedidoModel {
        return try {
            val response = api.postPedido("Bearer $token", pedido)
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
    suspend fun postPedidoTransferir(pedido: PedidoModel, token: String): PedidoModel {
        return try {
            val response = api.postPedidoTransferir("Bearer $token", pedido)
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
    suspend fun postDeletePedido(pedido: PedidoModel, token: String, passSupervisor: String): PedidoModel {
        return try {
            val response = api.postDeletePedido("Bearer $token", pedido, passSupervisor)
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
    suspend fun postPedidoDetalle(pedido: PedidoModel, token: String): PedidoModel {
        return try {
            val response = api.postPedidoDetalle("Bearer $token", pedido)
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
    suspend fun deletePedidoDetalle(pedido: PedidoModel, token: String, passSupervisor: String): PedidoModel {
        return try {
            val response = api.deletePedidoDetalle("Bearer $token", pedido, passSupervisor)
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
    suspend fun getPedidoById(id: String,token: String): PedidoModel {
        return try {
            val response = api.getPedidoById(token,id)
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
