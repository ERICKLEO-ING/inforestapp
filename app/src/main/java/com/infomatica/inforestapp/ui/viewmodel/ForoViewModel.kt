package com.infomatica.inforestapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomatica.inforestapp.data.model.AlertasModel
import com.infomatica.inforestapp.data.model.ModificadorModel
import com.infomatica.inforestapp.data.model.OfertaModel
import com.infomatica.inforestapp.domain.ClienteFacturadoRepositoryUseCase
import com.infomatica.inforestapp.domain.DocumentoProcesaUseCase
import com.infomatica.inforestapp.domain.MensajesAlertasUseCase
import com.infomatica.inforestapp.domain.OfertaForoUseCase
import com.infomatica.inforestapp.domain.PedidoConsultaApiUseCase
import com.infomatica.inforestapp.domain.PedidoConsultaProviderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForoViewModel @Inject constructor (
    private val mensajesAlertasUseCase: MensajesAlertasUseCase,
    private val ofertaForoUseCase: OfertaForoUseCase

) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    var resultAlertas =  MutableLiveData<List<AlertasModel>?>()
    var resultOfertas =  MutableLiveData<List<OfertaModel>?>()
    fun onCreate() {
        viewModelScope.launch {
            try{
                fetchAlertas()
                fetchOfertas()
            } catch (ex: Exception){
                println("error: ${ex.message}" )
            }
        }

    }
    fun fetchAlertas() {
        viewModelScope.launch {
            try{
                isLoading.value = true
                var result = mensajesAlertasUseCase()
                resultAlertas.postValue(result)
                isLoading.value = false
            } catch (ex: Exception){
                println("error: ${ex.message}" )
            }
        }

    }
    fun fetchOfertas() {
        viewModelScope.launch {
            try{
                isLoading.value = true
                var result = ofertaForoUseCase()
                resultOfertas.postValue(result)
                isLoading.value = false
            } catch (ex: Exception){
                println("error: ${ex.message}" )
            }
        }

    }
}