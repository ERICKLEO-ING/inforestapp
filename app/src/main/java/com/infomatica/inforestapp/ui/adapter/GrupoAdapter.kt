package com.infomatica.inforestapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.GrupoModel


class GrupoAdapter(
    private val listaGrupo: List<GrupoModel>,
    private val mItemListener: ItemClickListener
) : RecyclerView.Adapter<GrupoAdapter.ViewHolder>() {

    override fun getItemCount(): Int = listaGrupo.size

    interface ItemClickListener {
        fun onItemClick(details: GrupoModel?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_grupo, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val grupo = listaGrupo[position]
        holder.NameTextView.text = grupo.nombre
        holder.llgrupo.setBackgroundResource(if (grupo.seleccionado) R.color.colorBackground2 else R.color.colorPrimary)

        holder.itemView.setOnClickListener {
            listaGrupo.forEach { it.seleccionado = false }
            grupo.seleccionado =true
            notifyDataSetChanged()
            mItemListener.onItemClick(grupo)
        }
        // Bind other data to the views
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val NameTextView: TextView = itemView.findViewById(R.id.tv_nombregrupo)
        val llgrupo: LinearLayout = itemView.findViewById(R.id.llgrupo)
    }
}