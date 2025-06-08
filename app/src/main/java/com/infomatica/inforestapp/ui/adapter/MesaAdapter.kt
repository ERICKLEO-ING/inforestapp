package com.infomatica.inforestapp.ui.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.MesasModel

class MesaAdapter(
    private val mItemListener: ItemClickListener
) : RecyclerView.Adapter<MesaAdapter.ViewHolder>() {

    // Lista de mesas que se actualizará dinámicamente
    private var listaMesa: List<MesasModel> = emptyList()

    // Interfaz para gestionar los clics en los elementos
    interface ItemClickListener {
        fun onItemClick(details: MesasModel?)
    }

    // Método para establecer la lista de mesas y actualizar el adaptador con DiffUtil
    fun submitList(newMesas: List<MesasModel>) {
        val diffCallback = MesaDiffCallback(listaMesa, newMesas)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        listaMesa = newMesas
        diffResult.dispatchUpdatesTo(this) // Actualiza solo los elementos necesarios
    }

    override fun getItemCount(): Int = listaMesa.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_mesa, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mesa = listaMesa[position]
        holder.NameTextView.text = mesa.nombre

        holder.codigoTextView.text = mesa.codigo
        holder.PedidoTextView.text = if (mesa.codigopedido.isNullOrEmpty()) {
            "${mesa.pax} asientos"
        } else {
            "${mesa.codigopedido}"
        }
        if (mesa.codigopedido.isEmpty()){
            holder.horaTextView.isVisible = false
        }
        else{
            holder.horaTextView.isVisible = true
            holder.horaTextView.text ="${mesa.tiempo}" //mesa.tiempo
        }

        if(mesa.precuenta<=0){
            holder.precuentaTextView.isVisible = false
        } else {
            holder.precuentaTextView.isVisible = true
        }

        try {
            holder.linearLayout.setBackgroundColor(Color.parseColor(mesa.color))
            holder.NameTextView.setTextColor(Color.parseColor(mesa.colortexto))
            holder.PedidoTextView.setTextColor(Color.parseColor(mesa.colortexto))
        } catch (e: IllegalArgumentException) {
            Log.e("ColorError", "Color inválido: ${mesa.color}")
        }
        holder.itemView.setOnClickListener {
            mItemListener.onItemClick(mesa)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val NameTextView: TextView = itemView.findViewById(R.id.tv_nombre)
        val codigoTextView: TextView = itemView.findViewById(R.id.tv_codigo)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.ll_mesa)
        val PedidoTextView: TextView = itemView.findViewById(R.id.tv_pedido)
        val horaTextView: TextView = itemView.findViewById(R.id.tv_pedido_hora)
        val precuentaTextView: TextView = itemView.findViewById(R.id.tv_pedido_precuenta)
    }

    // Implementación de DiffUtil para comparar las listas de mesas
    class MesaDiffCallback(
        private val oldList: List<MesasModel>,
        private val newList: List<MesasModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].codigo == newList[newItemPosition].codigo
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
