package com.infomatica.inforestapp.data

import com.infomatica.inforestapp.data.model.AlertasModel
import com.infomatica.inforestapp.data.model.ImpresoraCajaModel
import com.infomatica.inforestapp.data.model.MensajeMeseroModel
import com.infomatica.inforestapp.data.model.MotivoDescuentoModel
import com.infomatica.inforestapp.data.model.MotivoDescuentoProvider
import com.infomatica.inforestapp.data.model.MotivoEliminacionModel
import com.infomatica.inforestapp.data.model.MotivoEliminacionProvider
import com.infomatica.inforestapp.data.model.OfertaModel
import com.infomatica.inforestapp.data.model.ParametrosModel
import com.infomatica.inforestapp.data.model.ParametrosProvider
import com.infomatica.inforestapp.data.model.TarjetaBancariaModel
import com.infomatica.inforestapp.data.model.TipoIdentidadModel
import com.infomatica.inforestapp.data.network.ParametroService
import javax.inject.Inject


class ParametroRepository @Inject constructor(
    private val service: ParametroService,
    private  val provider: ParametrosProvider,
    private  val motivoEliminacionProvider: MotivoEliminacionProvider,
    private  val motivoDescuentoProvider: MotivoDescuentoProvider
) {
    suspend fun getParametros():ParametrosModel{
        val response = service.getParametros()
        provider.parametro = response.first()
        return response.first()
    }
    suspend fun getMotivoEliminacion():List<MotivoEliminacionModel>{
        val response = service.getMotivoEliminacion()
        motivoEliminacionProvider.motivoEliminacion = response
        return response
    }
    suspend fun getMotivoDescuento():List<MotivoDescuentoModel>{
        val response = service.getMotivoDescuento()
        motivoDescuentoProvider.motivo = response
        return response
    }
    suspend fun getMensaje():List<AlertasModel>{
        val response = service.getMensaje()
        return response
    }
    suspend fun getOferta():List<OfertaModel>{
        val response = service.getOferta()
        return response
    }
    suspend fun getTarjetaBancaria():List<TarjetaBancariaModel>{
        val response = service.getTarjetaBancaria()
        return response
    }
    suspend fun getMensajeMesero():List<MensajeMeseroModel>{
        val response = service.getMensajeMesero()
        return response
    }
    suspend fun getImpresoraCaja(caja: String):List<ImpresoraCajaModel>{
        val response = service.getImpresoraCaja(caja)
        return response
    }

    suspend fun getTipoIdentidad():List<TipoIdentidadModel>{
        val response = service.getTipoIdentidad()
        return response
    }
    suspend fun getCajaMesero():List<ImpresoraCajaModel>{
        val response = service.getCajaMesero()
        return response
    }
}

