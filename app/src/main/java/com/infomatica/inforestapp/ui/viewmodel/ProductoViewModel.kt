package com.infomatica.inforestapp.ui.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomatica.inforestapp.GlobalVariables
import com.infomatica.inforestapp.data.model.ModificadorModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.domain.ModificadorListaUseCase
import com.infomatica.inforestapp.domain.PedidoConsultaProviderUseCase
import com.infomatica.inforestapp.domain.PedidoGeneraDetalleUseCase
import com.infomatica.inforestapp.domain.ProductoProviderActualizaUseCase
import com.infomatica.inforestapp.domain.ProductoProviderConsultaUseCase
import com.infomatica.inforestapp.domain.UsuarioConsultaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductoViewModel @Inject constructor (
    private var pedidoConsultaProviderUseCase: PedidoConsultaProviderUseCase,
    private var modificadorListaUseCase: ModificadorListaUseCase,
    private var pedidoGeneraDetalleUseCase: PedidoGeneraDetalleUseCase,
    private var usuarioConsultaUseCase: UsuarioConsultaUseCase,
    private var productoProviderConsultaUseCase: ProductoProviderConsultaUseCase
) : ViewModel() {

    val resultPedidoModel =  MutableLiveData<PedidoModel?>()
    val resultProductoModel =  MutableLiveData<ProductoModel?>()
    val resultModificadoresModel =  MutableLiveData<List<ModificadorModel>?>()
    val isLoading = MutableLiveData<Boolean>()

    fun onCreate() {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)

                val pedidoActual: PedidoModel? = pedidoConsultaProviderUseCase()
                pedidoActual?.status = 100
                resultPedidoModel.postValue(pedidoActual)

                val productoActual: ProductoModel? = productoProviderConsultaUseCase()
                if (productoActual != null) {
                    productoActual.codigo?.let {
                        if (pedidoActual != null) {
                            consultaModificadores(it, pedidoActual.codigopedido, if (productoActual.item.isNullOrEmpty()) "0" else productoActual.item )
                        }
                    }
                    productoActual.orden = GlobalVariables.ordenGeneral
                }
                resultProductoModel.postValue(productoActual)
                //resultProductoModel.value?.let { consultaModificadores(it.codigo) }
            } catch (e: Exception) {
                e.printStackTrace() // Esto muestra el error en Logcat
            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }
    }
    //region Modificadores de producto
    fun consultaModificadores(codigo: String,codigoPedido: String, item: String) {
        //val result = MutableLiveData<List<ModificadorModel>>()
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val result = modificadorListaUseCase(codigo,codigoPedido,item)
                if (result.isNotEmpty()) {
                    resultModificadoresModel.postValue(result)
                } else {
                    resultModificadoresModel.postValue(emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace() // Esto muestra el error en Logcat
            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }
        //return result
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

}