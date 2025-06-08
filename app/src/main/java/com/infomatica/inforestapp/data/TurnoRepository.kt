package com.infomatica.inforestapp.data

import com.infomatica.inforestapp.data.model.CanalVentaProvider
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.ResponseModel
import com.infomatica.inforestapp.data.model.TurnoModel
import com.infomatica.inforestapp.data.network.CanalVentaService
import com.infomatica.inforestapp.data.network.TurnoService
import com.infomatica.inforestapp.domain.UsuarioConsultaUseCase
import javax.inject.Inject

class TurnoRepository @Inject constructor(
    private val service: TurnoService,
    private  val usuarioConsultaUseCase: UsuarioConsultaUseCase
) {
    suspend fun getTurno(caja: String): TurnoModel {
        val usuario =usuarioConsultaUseCase()
        val response = usuario?.let { service.getTurno(usuario.token,caja) }
        return response?: TurnoModel(codigo = "Sin Turno")
    }
    suspend fun postTurno(caja: String): TurnoModel {
        val usuario =usuarioConsultaUseCase()
        val response = usuario?.let { service.postTurno(usuario.token,caja) }
        return response?: TurnoModel(codigo = "Sin Turno")
    }
    suspend fun postMarcacion(pass: String): ResponseModel {
        val response = service.postMarcacion(pass)
        return response?: ResponseModel(result = "1")
    }
}