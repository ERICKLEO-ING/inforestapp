package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class SubGrupoModel (
    @SerializedName("codigosubgrupo") val codigo: String,
    @SerializedName("nombresubgrupo") val nombre: String,
    var seleccionado: Boolean = false
)