package com.infomatica.inforestapp.data.network

import com.infomatica.inforestapp.data.model.AlertasModel
import com.infomatica.inforestapp.data.model.ImpresoraCajaModel
import com.infomatica.inforestapp.data.model.MensajeMeseroModel
import com.infomatica.inforestapp.data.model.ModificadorModel
import com.infomatica.inforestapp.data.model.MotivoDescuentoModel
import com.infomatica.inforestapp.data.model.MotivoEliminacionModel
import com.infomatica.inforestapp.data.model.OfertaModel
import com.infomatica.inforestapp.data.model.ParametrosModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.SalonModel
import com.infomatica.inforestapp.data.model.TarjetaBancariaModel
import com.infomatica.inforestapp.data.model.TipoIdentidadModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject


class ParametroService @Inject constructor(
    private val api:ParametroApiClient
){
    suspend fun  getParametros(): List<ParametrosModel>{
        return withContext(Dispatchers.IO){
            val response = api.getParametros()
            response.body()?: emptyList()
        }
    }

    suspend fun  getMotivoEliminacion(): List<MotivoEliminacionModel>{
        return withContext(Dispatchers.IO){
            val response = api.getMotivoEliminacion()
            response.body()?: emptyList()
        }
    }

    suspend fun  getMotivoDescuento(): List<MotivoDescuentoModel>{
        return withContext(Dispatchers.IO){
            val response = api.getMotivoDescuento()
            response.body()?: emptyList()
        }
    }
    suspend fun  getMensaje(): List<AlertasModel>{
        return withContext(Dispatchers.IO){
            val response = api.getMensaje()
            response.body()?: emptyList()
        }
    }
    suspend fun  getOferta(): List<OfertaModel>{
        return withContext(Dispatchers.IO){
            val response = api.getOferta()
            response.body()?: emptyList()
        }
    }
    suspend fun  getTarjetaBancaria(): List<TarjetaBancariaModel>{
        return withContext(Dispatchers.IO){
            val response = api.getTarjetaBancaria()
            response.body()?: emptyList()
        }
    }
    suspend fun  getMensajeMesero(): List<MensajeMeseroModel>{
        return withContext(Dispatchers.IO){
            val response = api.getMensajeMesero()
            response.body()?: emptyList()
        }
    }
    suspend fun  getImpresoraCaja(caja: String): List<ImpresoraCajaModel>{
        return withContext(Dispatchers.IO){
            val response = api.getImpresoraCaja(caja)
            response.body()?: emptyList()
        }
    }
    suspend fun  getTipoIdentidad(): List<TipoIdentidadModel>{
        return withContext(Dispatchers.IO){
            val response = api.getTipoIdentidad()
            response.body()?: emptyList()
        }
    }
    suspend fun  getCajaMesero(): List<ImpresoraCajaModel>{
        return withContext(Dispatchers.IO){
            val response = api.getCajaMesero()
            response.body()?: emptyList()
        }
    }
}