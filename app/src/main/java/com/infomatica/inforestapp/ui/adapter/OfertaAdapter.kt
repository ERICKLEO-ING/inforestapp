package com.infomatica.inforestapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.data.model.OfertaModel
import com.infomatica.inforestapp.databinding.ItemOfertaBinding


class OfertaAdapter(
) : ListAdapter<OfertaModel, OfertaAdapter.ViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOfertaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvNombreoferta.text = item.descripcion
        holder.binding.tvNombreofertaporcentaje.text = item.descuento
        holder.binding.tvHoradia.text = item.dias + " " + item.hora
    }

    inner class ViewHolder(val binding: ItemOfertaBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<OfertaModel>() {
        override fun areItemsTheSame(oldItem: OfertaModel, newItem: OfertaModel): Boolean {
            return oldItem.codigo == newItem.codigo // Asegúrate de que `id` es único
        }

        override fun areContentsTheSame(oldItem: OfertaModel, newItem: OfertaModel): Boolean {
            return oldItem == newItem // Compara todo el contenido
        }
    }
}