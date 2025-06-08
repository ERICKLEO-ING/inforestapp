package com.infomatica.inforestapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.data.model.SalonModel
import com.infomatica.inforestapp.data.model.UsuarioModel
import com.infomatica.inforestapp.domain.GetListaSalonUseCase
import com.infomatica.inforestapp.domain.PedidoConsultaApiUseCase
import com.infomatica.inforestapp.domain.PedidoConsultaProviderUseCase
import com.infomatica.inforestapp.domain.PedidoTransferirUseCase
import com.infomatica.inforestapp.domain.UsuarioConsultaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferirViewModel @Inject constructor(
    private var pedidoConsultaProviderUseCase: PedidoConsultaProviderUseCase,
    private var pedidoConsultaApiUseCase: PedidoConsultaApiUseCase,
    private val usuarioConsultaUseCase: UsuarioConsultaUseCase,
    private val pedidoTransferirUseCase: PedidoTransferirUseCase,
    private val getListaSalonUseCase: GetListaSalonUseCase
    ) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val resultPedidoModel =  MutableLiveData<PedidoModel?>()
    val listaSalon = MutableLiveData<List<SalonModel>?>()
    lateinit var resultusuario : UsuarioModel
    fun onCreate() {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                resultusuario = usuarioConsultaUseCase()!!
                val pedidoActual = pedidoConsultaProviderUseCase()
                pedidoActual?.status = 100
                resultPedidoModel.postValue(pedidoActual)
                isLoading.postValue(false)

            } catch (e: Exception) {
                e.printStackTrace() // this view el error en Logcat
            }
        }
    }
    // Método para obtener la lista de salones
    fun fetchSalonMesasList() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = getListaSalonUseCase()
                result?.let {
                    listaSalon.postValue(it)
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun transferirItemsPedidos(
        Items: List<ProductoModel>,
        codigoTransferir: String,
        obsTransferir: String,
        passwordSupervisor: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val pedidoActual = pedidoConsultaProviderUseCase()
                pedidoActual?.codigopedidotransferir = codigoTransferir
                pedidoActual?.observacionpedidotransferir = obsTransferir
                pedidoActual?.passwordsupervisor = passwordSupervisor
                pedidoActual?.detalle = Items.toMutableList()

                //resultusuario = usuarioConsultaUseCase()!!
                val result = pedidoActual?.let { pedidoTransferirUseCase(it, resultusuario.token) }
                resultPedidoModel.postValue(result)

                pedidoActual?.codigopedido?.let { pedidoConsultaApiUseCase(it) };

              } catch (e: Exception) {
                handleError(e)
                resultPedidoModel.postValue(PedidoModel(
                    99,
                    e.message.orEmpty(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    0,
                    ""
                ))
            } finally {
                isLoading.value = false
            }
        }
    }
    // Maneja errores y los imprime
    private fun handleError(e: Exception) {
        e.printStackTrace() // Se podría mejorar para enviar un mensaje de error más amigable a la UI
    }
}