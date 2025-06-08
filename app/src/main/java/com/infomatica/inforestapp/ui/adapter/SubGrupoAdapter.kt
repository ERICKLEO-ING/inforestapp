package com.infomatica.inforestapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.SubGrupoModel

class SubGrupoAdapter(
    private val listaSubGrupo: List<SubGrupoModel>,
    private val mItemListener: ItemClickListener
) : RecyclerView.Adapter<SubGrupoAdapter.ViewHolder>() {

    override fun getItemCount(): Int = listaSubGrupo.size

    interface ItemClickListener {
        fun onItemClick(details: SubGrupoModel?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_subgrupo, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subgrupo = listaSubGrupo[position]
        holder.NameTextView.text = subgrupo.nombre
        holder.llsubgrupo.setBackgroundResource(if (subgrupo.seleccionado) R.color.colorBackground2 else R.color.colorBackground)

        holder.itemView.setOnClickListener {
            listaSubGrupo.forEach { it.seleccionado = false }
            subgrupo.seleccionado =true
            notifyDataSetChanged()
            mItemListener.onItemClick(subgrupo)
        }

        // Bind other data to the views
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val NameTextView: TextView = itemView.findViewById(R.id.tv_nombresubgrupo)
        val llsubgrupo : LinearLayout = itemView.findViewById(R.id.llsubgrupo)
    }
}