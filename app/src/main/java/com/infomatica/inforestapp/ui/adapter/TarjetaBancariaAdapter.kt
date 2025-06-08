package com.infomatica.inforestapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.data.model.TarjetaBancariaModel
import com.infomatica.inforestapp.databinding.ItemTarjetaBinding

class TarjetaBancariaAdapter(
    private val mItemListener: ItemClickListener,
) : ListAdapter<TarjetaBancariaModel, TarjetaBancariaAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTarjetaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvnombretarjeta.text = item.descripcion
        holder.binding.cardview.setOnClickListener {
            mItemListener.onItemClick(item)
        }
//        holder.binding.tvdescripcionalerta.text = item.mensaje
    }
    interface ItemClickListener {
        fun onItemClick(details: TarjetaBancariaModel?)
    }
    inner class ViewHolder(val binding: ItemTarjetaBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<TarjetaBancariaModel>() {
        override fun areItemsTheSame(oldItem: TarjetaBancariaModel, newItem: TarjetaBancariaModel): Boolean {
            return oldItem.codigo == newItem.codigo // Asegúrate de que `id` es único
        }

        override fun areContentsTheSame(oldItem: TarjetaBancariaModel, newItem: TarjetaBancariaModel): Boolean {
            return oldItem == newItem // Compara el contenido
        }
    }
}