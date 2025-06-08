package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class ToppingModel(
    @SerializedName("codigotopping") val codigo: String = "",
    @SerializedName("itemtopping") val itemtopping: String = "",
    @SerializedName("nombretopping") val nombre: String = "",
    @SerializedName("iscantidadtopping") val iscantidad: Boolean = false,
    @SerializedName("monedatopping") val moneda: String="",
    @SerializedName("preciotopping") val precio: Double = 0.0,
    @SerializedName("cantidadtopping") var cantidad: Double = 0.0,
    @SerializedName("cargoauttopping") val cargoauttopping: Boolean = false,
    @SerializedName("eliminafijotopping") val eliminafijotopping: Boolean = false,
    @SerializedName("aumentatopping") var aumentatopping: Double = 0.0,
    @SerializedName("tienepropiedades") val tienepropiedades: Boolean = false,
    @SerializedName("modificadores") val modificadores: MutableList<ModificadorModel> = mutableListOf(),
    @SerializedName("observaciontopping") val observaciontopping: String = "",
    @SerializedName("descripciontercernivel") var descripciontercernivel: String = ""
)
