package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.ResponseModel
import com.infomatica.inforestapp.data.model.SalonModel
import com.infomatica.inforestapp.data.model.TurnoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TurnoService @Inject constructor(
    private val api:TurnoApiClient
) {
    suspend fun  getTurno(token: String,caja: String): TurnoModel?{
        return withContext(Dispatchers.IO){
            val response = api.getTurno("Bearer $token",caja)
            var respuesta = response.body()?.first()
            respuesta?: TurnoModel(codigo = "Sin Turno")
        }
    }

    suspend fun  postTurno(token: String,caja: String): TurnoModel?{
        return withContext(Dispatchers.IO){
            val response = api.postTurno("Bearer $token",caja)
            var respuesta = response.body()?.first()
            respuesta?: TurnoModel(codigo = "Sin Turno")
        }
    }

    suspend fun  postMarcacion(pass: String): ResponseModel?{
        return withContext(Dispatchers.IO){
            val response = api.postMarcacion(pass)
            var respuesta = response.body()?.first()
            respuesta?: ResponseModel(result = "1", mensaje = "vacio")
        }
    }
}