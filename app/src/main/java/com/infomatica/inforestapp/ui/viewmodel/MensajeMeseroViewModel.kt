package com.infomatica.inforestapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomatica.inforestapp.data.model.ImpresoraCajaModel
import com.infomatica.inforestapp.data.model.MensajeImpresionModel
import com.infomatica.inforestapp.data.model.MensajeMeseroModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.UsuarioModel
import com.infomatica.inforestapp.domain.ImpresoraCajaConsultaUseCase
import com.infomatica.inforestapp.domain.MensajeImpresionUseCase
import com.infomatica.inforestapp.domain.MensajeMeseroConsultaUseCase
import com.infomatica.inforestapp.domain.PedidoConsultaProviderUseCase
import com.infomatica.inforestapp.domain.UsuarioConsultaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MensajeMeseroViewModel @Inject constructor(
    private val pedidoConsultaProviderUseCase: PedidoConsultaProviderUseCase,
    private val mensajeMeseroConsultaUseCase: MensajeMeseroConsultaUseCase,
    private val impresoraCajaConsultaUseCase: ImpresoraCajaConsultaUseCase,
    private val usuarioConsultaUseCase: UsuarioConsultaUseCase,
    private val MensajeImpresionUseCase: MensajeImpresionUseCase
) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val resultPedidoModel = MutableLiveData<PedidoModel?>()
    val resultUsuarioModel = MutableLiveData<UsuarioModel?>()
    val resultMensajeMesero = MutableLiveData<List<MensajeMeseroModel>?>()
    val resultImpresoraCaja = MutableLiveData<List<ImpresoraCajaModel>?>()
    val resultMensajeImpresion = MutableLiveData<MensajeImpresionModel>()
    fun onCreate() {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val resultusuario = usuarioConsultaUseCase()!!
                resultUsuarioModel.postValue(resultusuario)

                val pedidoActual = pedidoConsultaProviderUseCase()
                pedidoActual?.status = 100
                resultPedidoModel.postValue(pedidoActual)

                fetchMensajeMesero()
                fetchImpresoraCaja()
                isLoading.postValue(false)

            } catch (e: Exception) {
                e.printStackTrace() // this view el error en Logcat
            }
        }
    }
    fun fetchMensajeMesero(
    ) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val result = mensajeMeseroConsultaUseCase()
                resultMensajeMesero.postValue(result);
            } catch (e: Exception) {
            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }

    }
    fun fetchImpresoraCaja(
    ) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val resultusuario = usuarioConsultaUseCase()!!
                val result = impresoraCajaConsultaUseCase(resultusuario.caja)
                resultImpresoraCaja.postValue(result);
            } catch (e: Exception) {
            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }

    }

    fun fetchImprimeMensaje( mensaje: MensajeImpresionModel ) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val resultusuario = usuarioConsultaUseCase()!!
                val pedidoActual = pedidoConsultaProviderUseCase()

                mensaje.usuario = resultusuario.usuario
                mensaje.codigoPedido = pedidoActual!!.codigopedido

                val result = MensajeImpresionUseCase(mensaje)
                resultMensajeImpresion.postValue(result)
            } catch (e: Exception) {

            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }
    }
}