package com.infomatica.inforestapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.databinding.ItemDescuentoBinding
import com.infomatica.inforestapp.data.model.MotivoDescuentoModel

class DescuentoAdapter(
    private val mItemListener: ItemClickListener,
    private val mItemEliminaListener: ItemClickListener
) : ListAdapter<MotivoDescuentoModel, DescuentoAdapter.ViewHolder>(DiffCallback()) {

    interface ItemClickListener {
        fun onItemClick(details: MotivoDescuentoModel?)
        fun onItemEliminaClick(details: MotivoDescuentoModel?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDescuentoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvnombredescuento.text = item.titulo
        holder.binding.tvdescripciondescuento.text = item.descripcion

        if (item.seleccionado) {
            holder.binding.llDescuento.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.border_verde);
        } else {
            holder.binding.llDescuento.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.border_gris);
        }
        if (item.aplicado) {
            holder.binding.ivEliminar.isVisible=true
            holder.binding.llDescuento.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.border_verde);
        } else {
            holder.binding.ivEliminar.isVisible=false

        }

        holder.binding.root.setOnClickListener {
            mItemListener.onItemClick(item)
        }
        holder.binding.ivEliminar.setOnClickListener {
            mItemEliminaListener.onItemEliminaClick(item)
        }
    }

    inner class ViewHolder(val binding: ItemDescuentoBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<MotivoDescuentoModel>() {
        override fun areItemsTheSame(oldItem: MotivoDescuentoModel, newItem: MotivoDescuentoModel): Boolean {
            return oldItem.codigo == newItem.codigo // Asegúrate de que `id` es único
        }

        override fun areContentsTheSame(oldItem: MotivoDescuentoModel, newItem: MotivoDescuentoModel): Boolean {
            return oldItem == newItem // Compara el contenido
        }
    }
}
