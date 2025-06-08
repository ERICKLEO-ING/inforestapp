package com.infomatica.inforestapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomatica.inforestapp.data.model.ClienteFacturadoModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.domain.ClienteFacturadoGuardarUseCase
import com.infomatica.inforestapp.domain.ClienteFacturadoRepositoryUseCase
import com.infomatica.inforestapp.domain.PedidoConsultaProviderUseCase
import com.infomatica.inforestapp.domain.PedidoDivisionGuardarProviderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DivisionViewModel @Inject constructor (
    private var pedidoConsultaProviderUseCase: PedidoConsultaProviderUseCase,
    private val clienteFacturadoRepositoryUseCase: ClienteFacturadoRepositoryUseCase,
    private var clienteFacturadoGuardarUseCase: ClienteFacturadoGuardarUseCase,
    private var pedidoDivisionGuardarProviderUseCase: PedidoDivisionGuardarProviderUseCase,
) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val resultPedidoModel =  MutableLiveData<PedidoModel?>()
    val resultclienteModel =  MutableLiveData<ClienteFacturadoModel?>()
    fun onCreate() {
        viewModelScope.launch {
            try{
                val pedidoActual = pedidoConsultaProviderUseCase()
                pedidoActual?.status = 100
                resultPedidoModel.postValue(pedidoActual)
            } catch (ex: Exception){
                println("error: ${ex.message}" )
            }
        }

    }
    fun consultarClienteRepository(
    ) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val result = clienteFacturadoRepositoryUseCase()
                resultclienteModel.postValue(result);
            } catch (e: Exception) {
            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }

    }
    fun guardarCliente(
        cliente: ClienteFacturadoModel
    ) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val result = clienteFacturadoGuardarUseCase(cliente)
                //clienteModel.postValue(result);
            } catch (e: Exception) {

            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }
    }
    fun guardarPedidoDivision(
        pedido: PedidoModel
    ) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val result = pedidoDivisionGuardarProviderUseCase(pedido)
                //clienteModel.postValue(result);
            } catch (e: Exception) {

            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }
    }
}