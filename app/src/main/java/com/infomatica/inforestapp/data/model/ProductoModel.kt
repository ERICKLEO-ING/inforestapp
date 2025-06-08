package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductoModel(
    @SerializedName("codigoproducto") val codigo: String? = null,
    @SerializedName("item") val item: String = "",
    @SerializedName("nombreproducto") val nombre: String = "-",
    @SerializedName("descripcion") val descripcion: String = "-",
    @SerializedName("moneda") val moneda: String = "",
    @SerializedName("cantidad") var cantidad: Double = 1.0,
    @SerializedName("preciounitario") var preciounitario: Double = 0.1,
    @SerializedName("precio") var precio: Double = 0.1,
    @SerializedName("itemventa") val itemventa: Double = 0.1,
    @SerializedName("lpropiedad") val lpropiedad: Int = 0,
    @SerializedName("isimpreso") val isimpreso: Boolean = false,
    @SerializedName("urlimagen") val urlimagen: String = "",
    @SerializedName("observacion") var observacion: String = "",
    @SerializedName("modificadores") var modificadores: MutableList<ModificadorModel> = mutableListOf(),
    @SerializedName("propiedades") var propiedades: String = "",
    @SerializedName("clientenombre") var clientenombre: String = "",
    @SerializedName("motivoanulacion") var motivoanulacion: String = "",
    @SerializedName("password") var password: String = "",
    @SerializedName("isselected") var isSelected: Boolean = false,
    val subProductos: MutableList<ProductoModel>? = mutableListOf(),
    val profundidad: Int = 0,
    var cantidadtransferir: Double = 0.0,
    @SerializedName("iscombo") val iscombo: Boolean = false,
    @SerializedName("cantidadmax") val cantidadmax: Double = 0.0,
    @SerializedName("cantidadmin") val cantidadmin: Double = 0.0,
    @SerializedName("lcontrolcritico") val lcontrolcritico:Boolean = false,
    @SerializedName("stockcritico") val stockcritico: Double = 0.0,
    @SerializedName("descuento") var descuento: Double = 0.0,
    @SerializedName("orden") var orden: Int = 0,
    @SerializedName("lpermitedescuento") var lpermitedescuento: Boolean = false,
    @SerializedName("lcambiaprecio") var lcambiaprecio: Boolean = false,
    @SerializedName("descuentounitario") var descuentounitario: Double = 0.0,
    var montocambiaprecio : Double = 0.00
) : Serializable
