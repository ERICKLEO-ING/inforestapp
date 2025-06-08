package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class MotivoDescuentoModel(
    @SerializedName("codigo") val codigo: String = "",
    @SerializedName("descripcion") val descripcion: String = "",
    @SerializedName("titulo") val titulo: String = "",
    @SerializedName("nratio") val nratio: Double = 0.0,
    @SerializedName("lratio") val lratio: Boolean = false,
    @SerializedName("ntope") val ntope: Double = 0.0,
    @SerializedName("ltopepedido") val ltopepedido: Boolean = false,
    @SerializedName("lbloqueo") val lbloqueo: Boolean = false,
    @SerializedName("activo") val activo: Boolean = false,
    var seleccionado: Boolean = false,
    var aplicado: Boolean = false
)