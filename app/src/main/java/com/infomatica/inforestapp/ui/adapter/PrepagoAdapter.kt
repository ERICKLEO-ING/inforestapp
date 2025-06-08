package com.infomatica.inforestapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.PrepagoModel
import com.infomatica.inforestapp.databinding.ItemPrepagoBinding

class PrepagoAdapter(
    private val mItemListener: ItemClickListener
) : ListAdapter<PrepagoModel, PrepagoAdapter.ViewHolder>(DiffCallback()) {

    interface ItemClickListener {
        fun onItemClickElimina(details: PrepagoModel?)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPrepagoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvMontoMultiple.text = item.monto.toString()
        holder.binding.tvReferenciaMultiple.text = item.numeroTarjeta
        holder.binding.tvIdListaMultiple.text = "Cobro ${position+1}"
        if (item.tipoPago=="02"){
            if (item.referencia=="QR"){
                holder.binding.ivListaMultiple.setImageResource(R.drawable.qr_pasivo_32x32)
            } else {
                holder.binding.ivListaMultiple.setImageResource(R.drawable.tarjeta_pasivo_32x32)
            }
        } else {
            holder.binding.ivListaMultiple.setImageResource(R.drawable.efectivo_pasivo_32x32)
        }
        holder.binding.ivEliminarMultiple.setOnClickListener {
            mItemListener.onItemClickElimina(item)
        }
    }

    inner class ViewHolder(val binding: ItemPrepagoBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<PrepagoModel>() {
        override fun areItemsTheSame(oldItem: PrepagoModel, newItem: PrepagoModel): Boolean {
            return oldItem.codigo == newItem.codigo // Asegúrate de que `id` es único
        }

        override fun areContentsTheSame(oldItem: PrepagoModel, newItem: PrepagoModel): Boolean {
            return oldItem == newItem // Compara todo el contenido
        }
    }
}