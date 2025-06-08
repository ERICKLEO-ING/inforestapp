package com.infomatica.inforestapp.ui.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.databinding.FragmentNombreclienteBinding

class NombreClienteAdapter(
    val listaNombres: MutableList<String>,
    private val mItemListener: ItemClickListener
) : RecyclerView.Adapter<NombreClienteAdapter.ViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(details: String?)
    }

    init {
        // IDs estables para correcto reciclaje
        setHasStableIds(true)
    }

    override fun getItemCount(): Int = listaNombres.size
    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentNombreclienteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = holder.bindingAdapterPosition
        if (pos == RecyclerView.NO_POSITION) return

        holder.binding.etNombrecliente.removeTextChangedListener(holder.textWatcher)
        val nombre = listaNombres[pos]

        with(holder.binding.etNombrecliente) {
            hint = "Cliente ${pos + 1}"
            setText(if (nombre != "Cliente") nombre else "")
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val adapterPos = holder.bindingAdapterPosition
                if (adapterPos != RecyclerView.NO_POSITION) {
                    val newText = s?.toString().orEmpty()
                    listaNombres[adapterPos] = if (newText.isBlank()) "Cliente" else newText
                   // updateListener(adapterPos, listaNombres[adapterPos])
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        holder.binding.etNombrecliente.addTextChangedListener(watcher)
        holder.textWatcher = watcher

        holder.binding.root.setOnClickListener {
            mItemListener.onItemClick(listaNombres[pos])
        }
    }

    /**
     * Devuelve la posición del primer elemento sin nombre real (placeholder "Cliente").
     * Retorna -1 si no hay elementos vacíos.
     */
    fun getFirstEmptyPosition(): Int {
        return listaNombres.indexOfFirst { it == "Cliente" }
    }

    inner class ViewHolder(val binding: FragmentNombreclienteBinding) : RecyclerView.ViewHolder(binding.root) {
        var textWatcher: TextWatcher? = null
    }
}
