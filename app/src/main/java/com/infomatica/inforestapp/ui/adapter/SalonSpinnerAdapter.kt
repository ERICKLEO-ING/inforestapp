package com.infomatica.inforestapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.infomatica.inforestapp.data.model.SalonModel

// Paso 1: Crear un adaptador personalizado para el Spinner
class SalonSpinnerAdapter(context: Context, private val salons: List<SalonModel>) : ArrayAdapter<SalonModel>(context, android.R.layout.simple_spinner_item, salons) {

    // Ajustar el espacio mínimo para el Spinner seleccionado
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val salon = salons[position]

        // Personaliza el texto del Spinner para que muestre el nombre del salon
        (view as TextView).text = salon.nombre

        return view
    }

    // Ajustar el espacio mínimo para los elementos del dropdown
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val salon = salons[position]

        // Personaliza el dropdown para mostrar el nombre del salon
        (view as TextView).text = salon.nombre

        // Asegurar que el espacio mínimo sea 50dp
        val params = view.layoutParams
        params.height = (50 * context.resources.displayMetrics.density).toInt() // 50dp a píxeles
        view.layoutParams = params

        return view
    }
}
