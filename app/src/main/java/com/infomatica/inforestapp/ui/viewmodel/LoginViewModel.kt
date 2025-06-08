package com.infomatica.inforestapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomatica.inforestapp.data.model.LoginModel
import com.infomatica.inforestapp.data.model.ParametrosModel
import com.infomatica.inforestapp.data.model.ResponseModel
import com.infomatica.inforestapp.data.model.UsuarioModel
import com.infomatica.inforestapp.data.network.ParametroService
import com.infomatica.inforestapp.domain.LoginUseCase
import com.infomatica.inforestapp.domain.MarcacionUseCase
import com.infomatica.inforestapp.domain.ParametroUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private  val parametroUseCase: ParametroUseCase,
    private  val marcacionUseCase: MarcacionUseCase
) : ViewModel() {

    val usuarioModel = MutableLiveData<UsuarioModel?>()
    val respuestaMarcacion = MutableLiveData<ResponseModel?>()
    val isLoading = MutableLiveData<Boolean>()
    var resultParametrosModel = MutableLiveData<ParametrosModel?>()
    fun onCreate() {
        viewModelScope.launch {
            try{
                isLoading.value = false
                val result = parametroUseCase()

                result.let {
                    resultParametrosModel.postValue(it)
                }
            } catch (ex: Exception){
               println("error: ${ex.message}" )
            }

        }

    }

    fun fetchLogin(username: String, password: String, iddispositivo: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val login = LoginModel(username, password, iddispositivo)
                val result = loginUseCase(login)
                usuarioModel.value = result
            } catch (e: Exception) {
                e.printStackTrace() // Muestra el error en Logcat
                usuarioModel.value = null // Asigna null en caso de error
            } finally {
                isLoading.value = false // Detiene el loader en todos los casos
            }
        }
    }
    fun registraMarcacion(pass: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val dato = marcacionUseCase(pass)
                respuestaMarcacion.postValue(dato)
            } catch (e: Exception) {
                e.printStackTrace() // Muestra el error en Logcat
                var er = ResponseModel(result = "1", mensaje = "vacio")
                respuestaMarcacion.postValue(er)
            } finally {
                isLoading.value = false // Detiene el loader en todos los casos
            }
        }
    }
}
