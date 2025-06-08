package com.infomatica.inforestapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.SalonModel

class SalonAdapter(
    private val mItemListener: (SalonModel?) -> Unit // Cambia a una lambda
) : ListAdapter<SalonModel, SalonAdapter.ViewHolder>(DiffCallback()) {

    interface ItemClickListener {
        fun onItemClick(details: SalonModel?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_salon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val salon = getItem(position)
        holder.bind(salon)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_nombre)
        private val codigoTextView: TextView = itemView.findViewById(R.id.tv_codigo)

        fun bind(salon: SalonModel) {
            nameTextView.text = salon.nombre
            codigoTextView.text = salon.codigo
            itemView.setOnClickListener {
                mItemListener(salon) // Llama a la lambda con el salón
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SalonModel>() {
        override fun areItemsTheSame(oldItem: SalonModel, newItem: SalonModel): Boolean {
            return oldItem.codigo == newItem.codigo // Compara el ID único
        }

        override fun areContentsTheSame(oldItem: SalonModel, newItem: SalonModel): Boolean {
            return oldItem == newItem // Compara el contenido completo
        }
    }
}
