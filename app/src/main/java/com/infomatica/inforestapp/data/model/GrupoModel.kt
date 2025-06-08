package com.infomatica.inforestapp.data.model

import com.google.gson.annotations.SerializedName

data class GrupoModel (
    @SerializedName("codigo") val codigo: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("subgrupos") val subgrupos: List<SubGrupoModel>,
    var seleccionado: Boolean = false
)
