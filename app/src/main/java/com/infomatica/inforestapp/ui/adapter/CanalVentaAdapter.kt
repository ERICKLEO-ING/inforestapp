package com.infomatica.inforestapp.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.infomatica.inforestapp.data.model.CanalVentaModel

class CanalVentaAdapter(context: Context, items: List<CanalVentaModel?>) :
    ArrayAdapter<CanalVentaModel>(context, android.R.layout.simple_spinner_item, items) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        // Muestra solo la descripción en la lista desplegable
        val objeto = getItem(position)
        (view as TextView).text = objeto?.Descripcion // Mostrar descripción en la lista desplegable
        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        // Muestra solo la descripción en el Spinner
        val tipoIdentidad = getItem(position)
        (view as TextView).text = tipoIdentidad?.Descripcion // Mostrar descripción en el Spinner
        return view
    }
}