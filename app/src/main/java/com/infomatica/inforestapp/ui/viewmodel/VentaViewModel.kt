package com.infomatica.inforestapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomatica.inforestapp.data.model.CanalVentaModel
import com.infomatica.inforestapp.data.model.MesasModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.SalonModel
import com.infomatica.inforestapp.data.model.UsuarioModel
import com.infomatica.inforestapp.domain.CanalVentaConsultaUseCase
import com.infomatica.inforestapp.domain.CerrarSesionUseCase
import com.infomatica.inforestapp.domain.GetListaMesaRepositoryUseCase
import com.infomatica.inforestapp.domain.GetListaSalonUseCase
import com.infomatica.inforestapp.domain.PedidoConsultaApiUseCase
import com.infomatica.inforestapp.domain.PedidoGeneraUseCase
import com.infomatica.inforestapp.domain.UsuarioConsultaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VentaViewModel @Inject constructor(
    private val getListaSalonUseCase: GetListaSalonUseCase,
    private val canalVentaConsultaUseCase: CanalVentaConsultaUseCase,
    private val getListaMesaRepositoryUseCase: GetListaMesaRepositoryUseCase,
    private val usuarioConsultaUseCase: UsuarioConsultaUseCase,
    private val pedidoGeneraUseCase: PedidoGeneraUseCase,
    private val pedidoConsultaApiUseCase: PedidoConsultaApiUseCase,
    private val cerrarSesionUseCase: CerrarSesionUseCase,
) : ViewModel() {

    val listaSalon = MutableLiveData<List<SalonModel>?>()
    val listMesa = MutableLiveData<List<MesasModel>?>()
    val isLoading = MutableLiveData<Boolean>()
    val disponiblesTexto = MutableLiveData<String>()
    val resultPedidoModel = MutableLiveData<PedidoModel?>()
    val usuarioActual = MutableLiveData<UsuarioModel?>()
    val salonSeleccionado = MutableLiveData<SalonModel?>()
    val resultCanalVentaModel = MutableLiveData<List<CanalVentaModel?>>()

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: MutableLiveData<String> = _mensajeError

    // Método onCreate para cargar datos al iniciar
    fun onCreate() {
        fetchSalonList()
        fetchUsuario()
        fetchCanalVentaList()
    }

    // Método para obtener la lista de salones
    fun fetchSalonList() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = getListaSalonUseCase()
                result?.let {
                    listaSalon.postValue(it)
                    updateMesasForSalon(it)
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                isLoading.value = false
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
    // Método para obtener la lista de TiposPedido
    fun cerrarSesion() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = cerrarSesionUseCase()

            } catch (e: Exception) {
                handleError(e)
            } finally {
                isLoading.value = false
            }
        }
    }
    // Actualiza las mesas del salón seleccionado y la cantidad de mesas disponibles
    private fun updateMesasForSalon(salones: List<SalonModel>) {
        val salonSeleccionadoValue = salonSeleccionado.value
        val salon = salones.find { it.codigo == salonSeleccionadoValue?.codigo }
        val mesas = salon?.mesas ?: salones.firstOrNull()?.mesas ?: emptyList()

        listMesa.postValue(mesas)
        updateDisponiblesText(mesas)
    }

    // Actualiza el texto que muestra la cantidad de mesas disponibles
    private fun updateDisponiblesText(mesas: List<MesasModel>) {
        val disponiblesCount = mesas.count { it.estado == "LISTA" || it.estado == "SUCIA" }
        val totalCount = mesas.size
        val salonName = salonSeleccionado.value?.nombre ?: ""
        disponiblesTexto.postValue("Disponibles: $disponiblesCount/$totalCount $salonName")
    }

    // Consulta el usuario actual
    private fun fetchUsuario() {
        val usuario = usuarioConsultaUseCase()
        usuarioActual.postValue(usuario)
    }

    // Consulta un pedido por su ID
    fun consultaPedido(id: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = pedidoConsultaApiUseCase(id)
                resultPedidoModel.postValue(result)
            } catch (e: Exception) {
                handleError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    // Genera un nuevo pedido
    fun generaPedido(codigomesa: String, canalventa: String, pax: Int, observacion: String, clientes: MutableList<String>) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val pedidoModel = PedidoModel(
                     0,
                     "",
                    "",
                     canalventa,
                     codigomesa,
                    "",
                    "",
                    pax,
                    observacion,
                    mutableListOf(),
                    clientes,
                    caja="000"
                )
                val result = pedidoGeneraUseCase(pedidoModel)
                resultPedidoModel.postValue(result)
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
    // Método para buscar mesa
    fun buscarMesa(nombreMesa: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = getListaMesaRepositoryUseCase() // Llamada al repositorio
                result?.let { listaSalones ->
                    // Filtrar mesas que coincidan con el nombre en cualquier salón
                    val mesasFiltradas = listaSalones.flatMap { salon ->
                        salon.mesas.filter { it.nombre.contains(nombreMesa, ignoreCase = true) }
                    }

                    if (mesasFiltradas.isNotEmpty()) {
                        // Publicar las mesas encontradas
                        listMesa.postValue(mesasFiltradas)
                    } else {
                        // Publicar lista vacía si no se encuentran mesas
                        listMesa.postValue(emptyList())
                        mensajeError.postValue("No se encontraron mesas con ese nombre")
                    }
                } ?: run {
                    // Manejar el caso de que no haya salones disponibles
                    listMesa.postValue(emptyList())
                    mensajeError.postValue("No hay salones disponibles")
                }
            } catch (e: Exception) {
                handleError(e) // Manejo de errores
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
