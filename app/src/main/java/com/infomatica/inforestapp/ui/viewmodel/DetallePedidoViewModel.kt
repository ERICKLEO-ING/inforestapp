package com.infomatica.inforestapp.ui.viewmodel

import LocalStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomatica.inforestapp.data.model.ImpresoraCajaModel
import com.infomatica.inforestapp.data.model.LoginModel
import com.infomatica.inforestapp.data.model.ModificadorModel
import com.infomatica.inforestapp.data.model.MotivoEliminacionModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.data.model.UsuarioModel
import com.infomatica.inforestapp.domain.ImpresoraCajaConsultaUseCase
import com.infomatica.inforestapp.domain.ImprimeComandaUseCase
import com.infomatica.inforestapp.domain.ImprimePrecuentaUseCase
import com.infomatica.inforestapp.domain.ModificadorListaUseCase
import com.infomatica.inforestapp.domain.MotivoEliminacionUsecase
import com.infomatica.inforestapp.domain.PedidoConsultaProviderUseCase
import com.infomatica.inforestapp.domain.PedidoDeleteDetalleUseCase
import com.infomatica.inforestapp.domain.PedidoDeleteUseCase
import com.infomatica.inforestapp.domain.PedidoGeneraDetalleUseCase
import com.infomatica.inforestapp.domain.ProductoProviderActualizaUseCase
import com.infomatica.inforestapp.domain.UsuarioConsultaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetallePedidoViewModel @Inject constructor (
    private var pedidoConsultaProviderUseCase: PedidoConsultaProviderUseCase,
    private var usuarioConsultaUseCase: UsuarioConsultaUseCase,
    private var pedidoDeleteDetalleUseCase: PedidoDeleteDetalleUseCase,
    private var pedidoGeneraDetalleUseCase: PedidoGeneraDetalleUseCase,
    private var pedidoDeleteUseCase: PedidoDeleteUseCase,
    private var imprimePrecuentaUseCase: ImprimePrecuentaUseCase,
    private  var imprimeComandaUseCase: ImprimeComandaUseCase,
    private val impresoraCajaConsultaUseCase: ImpresoraCajaConsultaUseCase,
    private  var motivoEliminacionUsecase: MotivoEliminacionUsecase,
    private val productoProviderActualizaUseCase: ProductoProviderActualizaUseCase // Inyección del Provider
) : ViewModel() {

    val resultPedidoModel =  MutableLiveData<PedidoModel?>()
    val resultUsuarioModel =  MutableLiveData<UsuarioModel?>()
    val resultEliminaPedidoModel =  MutableLiveData<PedidoModel?>()
    val isLoading = MutableLiveData<Boolean>()

    val resultModificadorModel =  MutableLiveData<List<ModificadorModel>?>()

    val resultImpresionPedidoModel =  MutableLiveData<PedidoModel?>()

    val resultImpresoraCaja = MutableLiveData<List<ImpresoraCajaModel>?>()

    val resultMotivoEliminacionModel =  MutableLiveData<List<MotivoEliminacionModel>?>()

    fun onCreate() {
        viewModelScope.launch {

            fetchImpresoraCaja()

            val datousuario = usuarioConsultaUseCase()
            resultUsuarioModel.postValue(datousuario)

            val pedidoActual: PedidoModel? = pedidoConsultaProviderUseCase()
            pedidoActual?.status = 100
            resultPedidoModel.postValue(pedidoActual)

            val resultelimina = motivoEliminacionUsecase()
            resultMotivoEliminacionModel.postValue(resultelimina)
        }

    }
    fun eliminaItemPedidoDetalle(
        detalleItem: ProductoModel
    ) {
        viewModelScope.launch {
            try {

                val datousuario = usuarioConsultaUseCase()
                val pedidoActual = pedidoConsultaProviderUseCase()
                isLoading.postValue(true)

                pedidoActual?.detalle = mutableListOf()
                pedidoActual?.detalle?.add(detalleItem)

                val result = pedidoActual?.let {
                    pedidoDeleteDetalleUseCase(it,datousuario?.token.toString(),"00")
                }
                result.let {
                    resultPedidoModel.postValue(result)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Esto muestra el error en Logcat
                resultPedidoModel.postValue(
                    PedidoModel(
                        99,
                        e.message.toString(),
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        "",
                        detalle = mutableListOf()
                    )
                )
            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }

    }

    fun actualizaPedidoDetalle(
        detalleItem: ProductoModel
    ) {
        viewModelScope.launch {
            try {

                val datousuario = usuarioConsultaUseCase()
                val pedidoActual = pedidoConsultaProviderUseCase()
                isLoading.postValue(true)

                pedidoActual?.detalle = mutableListOf()
                pedidoActual?.detalle?.add(detalleItem)

                val result = pedidoActual?.let { pedidoGeneraDetalleUseCase(it,datousuario?.token.toString()) }
                result.let {
                    resultPedidoModel.postValue(result)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Esto muestra el error en Logcat
                resultPedidoModel.postValue(
                    PedidoModel(
                        99,
                        e.message.toString(),
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        "",
                        detalle = mutableListOf()
                    )
                )
            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }
    }

    fun eliminaPedido(
        password: String,
        motivo: String
    ) {
        viewModelScope.launch {
            try {

                val datousuario = usuarioConsultaUseCase()
                val pedidoActual = pedidoConsultaProviderUseCase()
                isLoading.postValue(true)

                pedidoActual?.detalle = mutableListOf()
                pedidoActual?.observacion = motivo

                val result = pedidoActual?.let {
                    pedidoDeleteUseCase(it,datousuario?.token.toString(),password)
                }
                result.let {
                    resultEliminaPedidoModel.postValue(result)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Esto muestra el error en Logcat
                resultEliminaPedidoModel.postValue(
                    PedidoModel(
                        99,
                        e.message.toString(),
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        "",
                        detalle = mutableListOf()
                    )
                )
            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }

    }

    //region Modificadores de producto
//    fun consultaModificadores(codigo: String, item: String): LiveData<List<ModificadorModel>> {
//        val result = MutableLiveData<List<ModificadorModel>>()
//        viewModelScope.launch {
//            try {
//                isLoading.postValue(true)
//                val pedidoActual = pedidoConsultaProviderUseCase()
//                val CodigoPedido = pedidoActual?.codigopedido.toString()
//                val result = modificadorListaUseCase(codigo, CodigoPedido, item)
//                if (result.isNotEmpty()) {
//                    resultModificadorModel.postValue(result)
//                } else {
//                    resultModificadorModel.postValue(emptyList())
//                }
//
//
//            } catch (e: Exception) {
//                e.printStackTrace() // Esto muestra el error en Logcat
//            } finally {
//                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
//            }
//        }
//        return result
//    }
    //endregion

    //region Impresiones

    fun imprimeComanda() {
        viewModelScope.launch {
            try {
                val datousuario = usuarioConsultaUseCase()
                val pedidoActual = pedidoConsultaProviderUseCase()
                isLoading.postValue(true)

                val result = pedidoActual?.let { imprimeComandaUseCase(it,datousuario?.token.toString()) }
                result.let {
                   if (result?.status==0){
                       result.mensaje = "Comanda ${result.codigopedido} enviado!"
                   }
                    resultImpresionPedidoModel.postValue(result)
                    resultPedidoModel.postValue(result)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Esto muestra el error en Logcat
                resultImpresionPedidoModel.postValue(
                    PedidoModel(
                        99,
                        e.message.toString(),
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        "",
                        detalle = mutableListOf()
                    )
                )
            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }

    }

    fun imprimePrecuenta(codigoimpresora : String, tamano: Int, activapre: Boolean) {
        viewModelScope.launch {
            try {

                val datousuario = usuarioConsultaUseCase()
                val pedidoActual = pedidoConsultaProviderUseCase()
                isLoading.postValue(true)

                val result = pedidoActual?.let {
                    it.codigoimpresora = codigoimpresora
                    it.tamanoprinter = tamano
                    it.imprimeprecuenta = activapre
                    imprimePrecuentaUseCase(it,datousuario?.token.toString())
                }

                result.let {
                    if (result?.status==0){
                        result?.mensaje = "Precuenta ${result?.codigopedido} enviado!"
                    }
                    resultPedidoModel.postValue(result)
                    resultImpresionPedidoModel.postValue(result)

                }
            } catch (e: Exception) {
                e.printStackTrace() // Esto muestra el error en Logcat
                resultImpresionPedidoModel.postValue(
                    PedidoModel(
                        99,
                        e.message.toString(),
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        "",
                        detalle = mutableListOf()
                    )
                )
            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }

    }
    //endregion

    fun actualizarProducto(nuevoProducto: ProductoModel) {
        productoProviderActualizaUseCase(nuevoProducto);
    }
    fun generaPedidoDetalle(
        detalleItem: ProductoModel
    ) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val datousuario = usuarioConsultaUseCase()
                val pedidoActual = pedidoConsultaProviderUseCase()


                pedidoActual?.detalle = mutableListOf()
                pedidoActual?.detalle?.add(detalleItem)

                val result = pedidoActual?.let { pedidoGeneraDetalleUseCase(it,datousuario?.token.toString()) }
                result.let {
                    resultPedidoModel.postValue(result)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Esto muestra el error en Logcat
                resultPedidoModel.postValue(
                    PedidoModel(
                        99,
                        e.message.toString(),
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        "",
                        detalle = mutableListOf()
                    )
                )
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
}