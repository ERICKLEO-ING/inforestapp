package com.infomatica.inforestapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.data.model.AlertasModel
import com.infomatica.inforestapp.databinding.ItemAlertaBinding

class AlertaAdapter(
) : ListAdapter<AlertasModel, AlertaAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlertaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvnombrealerta.text = item.tiempo
        holder.binding.tvdescripcionalerta.text = item.mensaje
    }

    inner class ViewHolder(val binding: ItemAlertaBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<AlertasModel>() {
        override fun areItemsTheSame(oldItem: AlertasModel, newItem: AlertasModel): Boolean {
            return oldItem.codigo == newItem.codigo // Asegúrate de que `id` es único
        }

        override fun areContentsTheSame(oldItem: AlertasModel, newItem: AlertasModel): Boolean {
            return oldItem == newItem // Compara todo el contenido
        }
    }
}
