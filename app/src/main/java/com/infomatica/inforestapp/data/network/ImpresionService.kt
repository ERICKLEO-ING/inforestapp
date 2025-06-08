package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.MensajeImpresionModel
import com.infomatica.inforestapp.data.model.PedidoModel
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class ImpresionService  @Inject constructor(
    private val api: ImpresionApiClient
){


    suspend fun postComanda(pedido: PedidoModel, token: String): PedidoModel {
        return try {
            val response = api.postComanda("Bearer $token", pedido)
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
    suspend fun postPrecuenta(pedido: PedidoModel, token: String): PedidoModel {
        return try {
            val response = api.postPrecuenta("Bearer $token", pedido)
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
    suspend fun postLiquidacion(caja: String, token: String): String {
        return try {
            val response = api.postLiquidacion("Bearer $token", caja)

            if (response.isSuccessful) {
                response.body()?.string() ?: "Error: Respuesta vacía"
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalle"
                "Error: ${response.code()} - ${response.message()} - $errorBody"
            }
        } catch (e: HttpException) {
            "Error HTTP: ${e.code()} - ${e.message()}"
        } catch (e: Exception) {
            "Error inesperado: ${e.localizedMessage}"
        }
    }

    suspend fun postMensajeImpresion(mensaje: MensajeImpresionModel, token: String): MensajeImpresionModel {
        return try {
            val response = api.postMensajeImpresion("Bearer $token", mensaje)

            if (response.isSuccessful) {
                response.body() ?: MensajeImpresionModel(mensaje = "Error: Respuesta vacía")
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalle"
                MensajeImpresionModel(
                    status = response.code(),
                    mensaje = "Error: ${response.message()} - $errorBody"
                )
            }
        } catch (e: HttpException) {
            MensajeImpresionModel(
                status = e.code(),
                mensaje = "Error HTTP: ${e.message()}"
            )
        } catch (e: Exception) {
            MensajeImpresionModel(
                status = -1,
                mensaje = "Error inesperado: ${e.localizedMessage}"
            )
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