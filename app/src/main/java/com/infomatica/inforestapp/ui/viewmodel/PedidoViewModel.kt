package com.infomatica.inforestapp.ui.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomatica.inforestapp.data.model.CanalVentaModel
import com.infomatica.inforestapp.data.model.GrupoModel
import com.infomatica.inforestapp.data.model.ImpresoraCajaModel
import com.infomatica.inforestapp.data.model.ModificadorModel
import com.infomatica.inforestapp.data.model.MotivoEliminacionModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.data.model.UsuarioModel
import com.infomatica.inforestapp.domain.CanalVentaConsultaUseCase
import com.infomatica.inforestapp.domain.ImpresoraCajaConsultaUseCase
import com.infomatica.inforestapp.domain.ImprimeComandaUseCase
import com.infomatica.inforestapp.domain.ImprimePrecuentaUseCase
import com.infomatica.inforestapp.domain.ListaGrupoUseCase
import com.infomatica.inforestapp.domain.ListaProductoUseCase
import com.infomatica.inforestapp.domain.ModificadorListaUseCase
import com.infomatica.inforestapp.domain.MotivoEliminacionUsecase
import com.infomatica.inforestapp.domain.PedidoConsultaProviderUseCase
import com.infomatica.inforestapp.domain.PedidoDeleteDetalleUseCase
import com.infomatica.inforestapp.domain.PedidoDeleteUseCase
import com.infomatica.inforestapp.domain.PedidoGeneraDetalleUseCase
import com.infomatica.inforestapp.domain.PedidoGeneraUseCase
import com.infomatica.inforestapp.domain.ProductoProviderActualizaUseCase
import com.infomatica.inforestapp.domain.UsuarioConsultaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PedidoViewModel @Inject constructor(
    private var listaGrupoUseCase : ListaGrupoUseCase,
    private var listaProductoUseCase : ListaProductoUseCase,
    private var pedidoGeneraDetalleUseCase: PedidoGeneraDetalleUseCase,
    private var usuarioConsultaUseCase: UsuarioConsultaUseCase,
    private var pedidoConsultaProviderUseCase: PedidoConsultaProviderUseCase,
    private var modificadorListaUseCase: ModificadorListaUseCase,
    private val pedidoGeneraUseCase: PedidoGeneraUseCase,
    private val productoProviderActualizaUseCase: ProductoProviderActualizaUseCase, // Inyección del Provider
    private val canalVentaConsultaUseCase: CanalVentaConsultaUseCase,
    private var pedidoDeleteDetalleUseCase: PedidoDeleteDetalleUseCase,
    private  var motivoEliminacionUsecase: MotivoEliminacionUsecase,
    private var pedidoDeleteUseCase: PedidoDeleteUseCase,
    private var imprimePrecuentaUseCase: ImprimePrecuentaUseCase,
    private  var imprimeComandaUseCase: ImprimeComandaUseCase,
    private val impresoraCajaConsultaUseCase: ImpresoraCajaConsultaUseCase,
) : ViewModel() {
    val listaGrupo= MutableLiveData<List<GrupoModel>?>()
    val listaProductos= MutableLiveData<List<ProductoModel>?>()
    val resultPedidoModel =  MutableLiveData<PedidoModel?>()
    val isLoading = MutableLiveData<Boolean>()
    val resultModificadorModel =  MutableLiveData<List<ModificadorModel>?>()
    val resultCanalVentaModel = MutableLiveData<List<CanalVentaModel?>>()

    val resultMotivoEliminacionModel =  MutableLiveData<List<MotivoEliminacionModel>?>()
    val resultEliminaPedidoModel =  MutableLiveData<PedidoModel?>()

    val resultImpresionPedidoModel =  MutableLiveData<PedidoModel?>()
    val resultImpresoraCaja = MutableLiveData<List<ImpresoraCajaModel>?>()

    val resultUsuarioModel =  MutableLiveData<UsuarioModel?>()

    fun onCreate() {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                fetchImpresoraCaja()

                val datousuario = usuarioConsultaUseCase()
                resultUsuarioModel.postValue(datousuario)

                val result = listaGrupoUseCase()

                if(!result.isNullOrEmpty()){
                    listaGrupo.postValue(result)
                    isLoading.postValue(false)
                }
                val pedidoActual = pedidoConsultaProviderUseCase()
                pedidoActual?.status = 100
                resultPedidoModel.postValue(pedidoActual)

                fetchListaProductos(pedidoActual!!.canalventa,"0101")
                fetchCanalVentaList()

                val resultelimina = motivoEliminacionUsecase()
                resultMotivoEliminacionModel.postValue(resultelimina)

            } catch (e: Exception) {
                e.printStackTrace() // this view el error en Logcat
            }
        }
    }
    // Método para obtener la lista de TiposPedido
    fun fetchCanalVentaList() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = canalVentaConsultaUseCase()
                result?.let {
                    resultCanalVentaModel.postValue(it)
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                isLoading.value = false
            }
        }
    }
    fun fetchPedidoActual(){
        viewModelScope.launch {
            val pedidoActual = pedidoConsultaProviderUseCase()
            pedidoActual?.status = 100
            resultPedidoModel.postValue(pedidoActual)
        }
    }
    // New function to fetch listaProductos from the API
    fun fetchListaProductos(canalventa: String, codigoSubGrupo: String) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                listaProductoUseCase(canalventa, codigoSubGrupo)?.let { resultproducto ->
                    listaProductos.postValue(resultproducto)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the error in Logcat
            } finally {
                isLoading.postValue(false)
            }
        }
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
    //region Modificadores de producto
    fun consultaModificadores(codigo: String): LiveData<List<ModificadorModel>> {
        val result = MutableLiveData<List<ModificadorModel>>()
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val result = modificadorListaUseCase(codigo,"0","0")
                if (result.isNotEmpty()) {
                    resultModificadorModel.postValue(result)
                } else {
                    resultModificadorModel.postValue(emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace() // Esto muestra el error en Logcat
            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }
        return result
    }


    //endregion

    fun modificaPedido(pedidoModel: PedidoModel) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val usuario = usuarioConsultaUseCase()
                val result = pedidoGeneraUseCase(pedidoModel)
                resultPedidoModel.postValue(result)
            } catch (e: Exception) {
                handleError(e)
            } finally {
                isLoading.value = false
            }
        }
    }
    fun actualizarProducto(nuevoProducto: ProductoModel) {
        productoProviderActualizaUseCase(nuevoProducto);
    }
    // Maneja errores y los imprime
    private fun handleError(e: Exception) {
        e.printStackTrace() // Se podría mejorar para enviar un mensaje de error más amigable a la U
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
                    it.tamanoprinter =tamano
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
}