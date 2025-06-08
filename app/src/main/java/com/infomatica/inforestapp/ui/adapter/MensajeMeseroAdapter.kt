package com.infomatica.inforestapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.MensajeMeseroModel
import com.infomatica.inforestapp.databinding.ItemTarjetaBinding

class MensajeMeseroAdapter(
    private val mItemListener: ItemClickListener,
) : ListAdapter<MensajeMeseroModel, MensajeMeseroAdapter.ViewHolder>(DiffCallback()) {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTarjetaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val context = holder.itemView.context

        holder.binding.tvnombretarjeta.text = item.descripcion

        // Aplicar color según si está seleccionado o no
        if (position == selectedPosition) {
            holder.binding.tvnombretarjeta.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            holder.binding.cardview.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorBackground2))
        } else {
            holder.binding.tvnombretarjeta.setTextColor(ContextCompat.getColor(context, R.color.lightgray_600))
            holder.binding.cardview.setCardBackgroundColor(ContextCompat.getColor(context, R.color.ColorFondoPantalla))
        }

        holder.binding.cardview.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            // Notificar para refrescar el cambio de colores
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            mItemListener.onItemClick(item)
        }
    }
    interface ItemClickListener {
        fun onItemClick(details: MensajeMeseroModel?)
    }
    inner class ViewHolder(val binding: ItemTarjetaBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<MensajeMeseroModel>() {
        override fun areItemsTheSame(oldItem: MensajeMeseroModel, newItem: MensajeMeseroModel): Boolean {
            return oldItem.codigo == newItem.codigo // Asegúrate de que `id` es único
        }

        override fun areContentsTheSame(oldItem: MensajeMeseroModel, newItem: MensajeMeseroModel): Boolean {
            return oldItem == newItem // Compara el contenido
        }
    }
}