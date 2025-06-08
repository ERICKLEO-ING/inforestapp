package com.infomatica.inforestapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomatica.inforestapp.data.model.CheckActivityResultDataNiubiz
import com.infomatica.inforestapp.data.model.ClienteFacturadoModel
import com.infomatica.inforestapp.data.model.ImpresoraCajaModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.PrepagoModel
import com.infomatica.inforestapp.data.model.TarjetaBancariaModel
import com.infomatica.inforestapp.data.model.TransactionParamsNiubiz
import com.infomatica.inforestapp.data.model.UsuarioModel
import com.infomatica.inforestapp.domain.ClienteFacturadoRepositoryUseCase
import com.infomatica.inforestapp.domain.DocumentoProcesaUseCase
import com.infomatica.inforestapp.domain.ImpresoraCajaConsultaUseCase
import com.infomatica.inforestapp.domain.PedidoConsultaApiUseCase
import com.infomatica.inforestapp.domain.PedidoConsultaProviderUseCase
import com.infomatica.inforestapp.domain.PedidoDivisionConsultaProviderUseCase
import com.infomatica.inforestapp.domain.PrepagoEliminaUseCase
import com.infomatica.inforestapp.domain.PrepagoProcesaUseCase
import com.infomatica.inforestapp.domain.TarjetaBancariaConsultaUseCase
import com.infomatica.inforestapp.domain.UsuarioConsultaUseCase
import com.infomatica.inforestapp.ui.fragment.TarjetaBancariaFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CobroViewModel @Inject constructor (
    private val clienteFacturadoRepositoryUseCase: ClienteFacturadoRepositoryUseCase,
    private var pedidoConsultaProviderUseCase: PedidoConsultaProviderUseCase,
    private var pedidoConsultaApiUseCase: PedidoConsultaApiUseCase,
    private  var documentoProcesaUseCase: DocumentoProcesaUseCase,
    private var usuarioConsultaUseCase: UsuarioConsultaUseCase,
    private  var prepagoProcesaUseCase: PrepagoProcesaUseCase,
    private var prepagoEliminaUseCase: PrepagoEliminaUseCase,
    private var tarjetaBancariaConsultaUseCase: TarjetaBancariaConsultaUseCase,
    private val impresoraCajaConsultaUseCase: ImpresoraCajaConsultaUseCase,
    private val pedidoDivisionConsultaProviderUseCase: PedidoDivisionConsultaProviderUseCase
) : ViewModel() {

    val clienteModel =  MutableLiveData<ClienteFacturadoModel?>()
    val isLoading = MutableLiveData<Boolean>()
    val resultPedidoModel =  MutableLiveData<PedidoModel?>()
    val resultUsuario =  MutableLiveData<UsuarioModel?>()
    val resultFacturacionModel =  MutableLiveData<PedidoModel?>()
    val resultImpresoraCaja = MutableLiveData<List<ImpresoraCajaModel>?>()
    val resultPrepagos =  MutableLiveData<List<PrepagoModel>>()
    val resultTarjetasBancarias =  MutableLiveData<List<TarjetaBancariaModel>>()

    fun onCreate(tipoProceso: String) {
        viewModelScope.launch {
            fetchImpresoraCaja()
            consultarClienteRepository()
            consultaTarjetaBancarias()
            consultaPedidoFacturar(tipoProceso)

            val datousuario = usuarioConsultaUseCase()
            resultUsuario.postValue(datousuario)
        }
    }
    fun consultarClienteRepository(
    ) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val result = clienteFacturadoRepositoryUseCase()
                clienteModel.postValue(result);
            } catch (e: Exception) {
            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }

    }
    fun consultaPedidoFacturar(tipoProceso: String){
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                var result: PedidoModel? = null
                if (tipoProceso=="division"){
                    ///pedidoActual =  pedidoConsultaProviderUseCase()
                    result = pedidoDivisionConsultaProviderUseCase()
                } else {
                    result =  pedidoConsultaProviderUseCase()
                }
//                var pedidoActual =  pedidoConsultaProviderUseCase()
//                var result = pedidoActual?.codigopedido?.let { pedidoConsultaApiUseCase(it) };
                resultPedidoModel.postValue(result);
                procesaPrepago(PrepagoModel(codigo="",documento = result!!.codigopedido, tipoPago = "", monto = 0.0, propina = 0.0, codigoTarjeta = "", numeroTarjeta = "", referencia = "", numeroATarjeta = "", banco = ""),tipoProceso)

            } catch (e: Exception) {

            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }
    }
    fun consultaTarjetaBancarias(){
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                var result = tarjetaBancariaConsultaUseCase();
                resultTarjetasBancarias.postValue(result);
            } catch (e: Exception) {

            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }
    }
    fun procesaPrepago(prepago: PrepagoModel, tipoProceso: String){
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                //var pedidoActual: PedidoModel?= null
                //var pedidoDivision = pedidoDivisionConsultaProviderUseCase()
//                if (tipoProceso=="division"){
//                    pedidoActual = pedidoDivisionConsultaProviderUseCase()
//                } else {
//                    pedidoActual = pedidoConsultaProviderUseCase()
//                }
                val pedidoActual = pedidoConsultaProviderUseCase()
                prepago.documento = pedidoActual!!.codigopedido
                val result = prepagoProcesaUseCase(prepago)
                resultPrepagos.postValue(result);

            } catch (e: Exception) {

            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }
    }
    fun eliminaPrepago(prepago: PrepagoModel){
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val pedidoActual = pedidoConsultaProviderUseCase()
                prepago.documento = pedidoActual!!.codigopedido
                val result = prepagoEliminaUseCase(prepago)
                resultPrepagos.postValue(result);

            } catch (e: Exception) {

            } finally {
                isLoading.postValue(false) // Asegura que el loader se detenga en todos los casos
            }
        }
    }

    fun procesoPedidoFacturar( prepago: PrepagoModel, prefijo: String, codigoimpresora: String, numerocopia: Int, tamano: Int, activadoc: Boolean, _tipoProceso: String){
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                var pedidoActual: PedidoModel? = null
                var pedidoReal = pedidoConsultaProviderUseCase()
                if (_tipoProceso=="division"){
                    pedidoActual = pedidoDivisionConsultaProviderUseCase()
                } else {
                     pedidoActual = pedidoConsultaProviderUseCase()
                }
                pedidoActual!!.codigopedido = pedidoReal!!.codigopedido
                pedidoActual!!.cajafacturacion = ""
                pedidoActual!!.prefijofacturacion = prefijo
                pedidoActual!!.codigoimpresora = codigoimpresora
                pedidoActual!!.numerocopia = numerocopia
                pedidoActual!!.tamanoprinter = tamano
                pedidoActual!!.imprimedocumento =  activadoc

                val resultCliente = clienteFacturadoRepositoryUseCase()

                pedidoActual?.apply {
                    resultCliente?.let {
                        if (_tipoProceso!="division")
                            clientefacturado = it
                    }
                    prepagos = mutableListOf(prepago)
                }

               val result = pedidoActual?.let { documentoProcesaUseCase(it,32) }
                resultFacturacionModel.postValue(result)

            } catch (e: Exception) {
                println(e.message)
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