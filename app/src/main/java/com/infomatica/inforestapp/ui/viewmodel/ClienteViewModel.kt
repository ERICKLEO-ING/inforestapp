package com.infomatica.inforestapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomatica.inforestapp.data.model.ClienteFacturadoModel
import com.infomatica.inforestapp.data.model.ParametrosModel
import com.infomatica.inforestapp.data.model.TipoIdentidadModel
import com.infomatica.inforestapp.data.model.UsuarioModel
import com.infomatica.inforestapp.domain.ClienteFacturadoGuardarUseCase
import com.infomatica.inforestapp.domain.ClienteFacturadoUseCase
import com.infomatica.inforestapp.domain.ParametroUseCase
import com.infomatica.inforestapp.domain.PedidoConsultaProviderUseCase
import com.infomatica.inforestapp.domain.TipoIdentidadConsultaUseCase
import com.infomatica.inforestapp.domain.UsuarioConsultaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClienteViewModel @Inject constructor (
    private var pedidoConsultaProviderUseCase: PedidoConsultaProviderUseCase,
    private var clienteFacturadoUseCase: ClienteFacturadoUseCase,
    private var clienteFacturadoGuardarUseCase: ClienteFacturadoGuardarUseCase,
    private var tipoIdentidadConsultaUseCase: TipoIdentidadConsultaUseCase,
    private var usuarioConsultaUseCase: UsuarioConsultaUseCase,
    private  val parametroUseCase: ParametroUseCase,
    ) : ViewModel() {

    val clienteModel =  MutableLiveData<ClienteFacturadoModel?>()
    val resultTipoIdentidad =  MutableLiveData<List<TipoIdentidadModel>?>()
    val isLoading = MutableLiveData<Boolean>()
    val resultUsuario =  MutableLiveData<UsuarioModel?>()
    var resultParametrosModel = MutableLiveData<ParametrosModel?>()
    fun onCreate() {
        viewModelScope.launch {
            fectTipoIdentidad()
            val datousuario = usuarioConsultaUseCase()
            resultUsuario.postValue(datousuario)

            val result = parametroUseCase()

            result.let {
                resultParametrosModel.postValue(it)
            }
        }
    }

    fun consultarCliente(
        codigo: String
    ) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val result = clienteFacturadoUseCase(codigo)
                clienteModel.postValue(result);
            } catch (e: Exception) {

            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }
    }
    fun fectTipoIdentidad(
    ) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val result = tipoIdentidadConsultaUseCase()
                resultTipoIdentidad.postValue(result);
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
}
