package com.infomatica.inforestapp.domain

import com.infomatica.inforestapp.data.ImpresionRepository
import com.infomatica.inforestapp.data.model.MensajeImpresionModel
import javax.inject.Inject

class MensajeImpresionUseCase @Inject constructor(
    private val repository: ImpresionRepository
){
    suspend operator fun invoke(mensaje: MensajeImpresionModel): MensajeImpresionModel = repository.postMensajeImpresion(mensaje)
}