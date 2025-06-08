package com.infomatica.inforestapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.TurnoModel
import com.infomatica.inforestapp.domain.ClienteFacturadoGuardarUseCase
import com.infomatica.inforestapp.domain.ClienteFacturadoUseCase
import com.infomatica.inforestapp.domain.ImprimeLiquidacionUseCase
import com.infomatica.inforestapp.domain.PedidoConsultaProviderUseCase
import com.infomatica.inforestapp.domain.TurnoCerrarUseCase
import com.infomatica.inforestapp.domain.TurnoConsultaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TurnoViewModel @Inject constructor (
    private var pedidoConsultaProviderUseCase: PedidoConsultaProviderUseCase,
    private var turnoConsultaUseCase: TurnoConsultaUseCase,
    private var imprimeLiquidacionUseCase: ImprimeLiquidacionUseCase,
    private  var turnoCerrarUseCase: TurnoCerrarUseCase
) : ViewModel(){
    val isLoading = MutableLiveData<Boolean>()
    //val resultPedidoModel =  MutableLiveData<PedidoModel?>()
    val resultTurno =  MutableLiveData<TurnoModel>()
    val resultTurnoCierre =  MutableLiveData<TurnoModel>()

    val resultLiquidacion =  MutableLiveData<String>()

    fun onCreate(caja: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            val turnoRespuesta =turnoConsultaUseCase.invoke(caja)
            resultTurno.postValue(turnoRespuesta)
            isLoading.postValue(false)
            //resultPedidoModel.postValue(pedidoActual)
        }
    }
    fun imprimeLiquidacion(caja: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            val respuesta =imprimeLiquidacionUseCase.invoke(caja)
            resultLiquidacion.postValue(respuesta)
            isLoading.postValue(false)
        }
    }
    fun cerrarTurno(caja: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            val respuesta =turnoCerrarUseCase.invoke(caja)
            resultTurnoCierre.postValue(respuesta)
            isLoading.postValue(false)
        }
    }
}