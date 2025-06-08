package com.infomatica.inforestapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomatica.inforestapp.data.model.MotivoDescuentoModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.domain.MotivoDescuentoUseCase
import com.infomatica.inforestapp.domain.PedidoConsultaProviderUseCase
import com.infomatica.inforestapp.domain.PedidoGeneraUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DescuentoViewModel  @Inject constructor (
    private var motivoDescuentoUseCase: MotivoDescuentoUseCase,
    private  var pedidoGeneraUseCase: PedidoGeneraUseCase,
    private  var pedidoConsultaProviderUseCase: PedidoConsultaProviderUseCase,
) : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    val resultMotivoDescuentoModel =  MutableLiveData<List<MotivoDescuentoModel>?>()
    val resultPedidoModel =  MutableLiveData<PedidoModel?>()
    fun onCreate() {
        viewModelScope.launch {
            isLoading.postValue(true)
            val pedidoActual = pedidoConsultaProviderUseCase()
            if (pedidoActual != null) {
                pedidoActual.status = 100
                resultPedidoModel.postValue(pedidoActual)
            }
            val result  =motivoDescuentoUseCase()
            result.forEach {
                it.aplicado = pedidoActual?.codigodescuento == it.codigo
            }

            resultMotivoDescuentoModel.postValue(result)

            isLoading.postValue(false)
        }
    }

    fun modificaPedido(codigodescuento:String, passworddescuento:String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val pedidoActual = pedidoConsultaProviderUseCase()

                if (pedidoActual != null) {
                    pedidoActual.codigodescuento = codigodescuento
                    pedidoActual.passwordsupervisor = passworddescuento
                    val result = pedidoGeneraUseCase(pedidoActual)
                    resultPedidoModel.postValue(result)
                }

            } catch (e: Exception) {

            } finally {
                isLoading.value = false
            }
        }
    }
}