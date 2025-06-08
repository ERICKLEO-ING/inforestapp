package com.infomatica.inforestapp.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.MesasModel
import com.infomatica.inforestapp.databinding.ItemTarjetaBinding

class MesaTransferirAdapter(
    private val mItemListener: ItemClickListener
) : ListAdapter<MesasModel, MesaTransferirAdapter.ViewHolder>(DiffCallback()) {

    private var selectedPosition: Int = -1 // Variable para guardar la posición seleccionada

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTarjetaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = getItem(position)
        holder.binding.tvnombretarjeta.text = item.nombre

        // Cambiar el color de fondo según si el ítem está seleccionado
        if (position == selectedPosition) {
            holder.binding.cardview.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.colorBackground2) // Color de selección
            )
        } else {
            holder.binding.cardview.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.ColorFondoPantalla) // Color por defecto
            )
        }

        holder.binding.cardview.setOnClickListener {
            selectedPosition = position // Actualizar la posición seleccionada
            mItemListener.onItemClick(item) // Llamar al listener con el ítem seleccionado
            notifyDataSetChanged() // Notificar al Adapter para actualizar la vista
        }
    }

    interface ItemClickListener {
        fun onItemClick(details: MesasModel?)
    }

    inner class ViewHolder(val binding: ItemTarjetaBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<MesasModel>() {
        override fun areItemsTheSame(oldItem: MesasModel, newItem: MesasModel): Boolean {
            return oldItem.codigo == newItem.codigo // Asegúrate de que `codigo` es único
        }

        override fun areContentsTheSame(oldItem: MesasModel, newItem: MesasModel): Boolean {
            return oldItem == newItem // Compara el contenido
        }
    }
}
