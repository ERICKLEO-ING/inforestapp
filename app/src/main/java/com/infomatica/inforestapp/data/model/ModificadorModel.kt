package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ModificadorModel(
    @SerializedName("status") var status: Int=100,
    @SerializedName("message") val mensaje: String="",
    @SerializedName("codigoproducto") val codigoproducto: String = "",
    @SerializedName("codigomodificador") val codigo: String = "",
    @SerializedName("nombremodificador") val nombre: String = "",
    @SerializedName("isobligatoriomodificador") val isobligatorio: Boolean = false,
    @SerializedName("isnoeliminamodificador") val isnoeliminamodificador: Boolean = false,
    @SerializedName("iscombomodificador") val iscombomodificador: Boolean = false,
    @SerializedName("maxenmodificador") var maximo: Int = 0,
    var hastaCombo: Int = 0,
    @SerializedName("toppings") var toppings: MutableList<ToppingModel> = mutableListOf(),
    var isExpanded: Boolean = true
) : Serializable