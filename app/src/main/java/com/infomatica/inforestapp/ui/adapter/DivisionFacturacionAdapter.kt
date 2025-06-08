package com.infomatica.inforestapp.ui.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.AlertasModel
import com.infomatica.inforestapp.data.model.MotivoDescuentoModel
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.databinding.ItemAlertaBinding
import com.infomatica.inforestapp.databinding.ItemDivisionBinding
import com.infomatica.inforestapp.ui.adapter.DescuentoAdapter.ItemClickListener


class DivisionFacturacionAdapter(
    private val mItemListener: ItemClickListener,
) : ListAdapter<PedidoModel, DivisionFacturacionAdapter.ViewHolder>(DiffCallback()) {

    interface ItemClickListener {
        fun onItemEditaClick(item: PedidoModel?)

        fun onItemEditaClienteClick(item: PedidoModel?)

        fun onItemCobrarClick(item: PedidoModel?)
    }

    // State flag moved from constructor to mutable property
    private var isCobrarMode: Boolean = false

    /**
     * Call this method to toggle cobrar mode from outside
     */
    fun setCobrarMode(enabled: Boolean) {
        isCobrarMode = enabled
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDivisionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvclientedivision.setText(item.clientedivision)
        holder.binding.lldivisionfacturacion.isVisible = item.isdivisionmonto
        if (item.isdivisionmonto){
            holder.binding.tvmontodivision.setText("${item.monedapedido}${String.format("%.2f", item.montodivision)}")
            holder.binding.ivEditamontodivision.setImageResource(R.drawable.editar_20x20)
        } else {
            if (item.detalle.filter { x -> x.isSelected }.isEmpty()){
                holder.binding.tvmontodivision.setText("Elige productos")
            } else {
                holder.binding.tvmontodivision.setText("${item.detalle.filter { x -> x.isSelected }.size} items")
            }
            //holder.binding.tvmontodivision.setText("Elige productos")
            holder.binding.ivEditamontodivision.setImageResource(R.drawable.fact_flecha_activo_24x24)
        }
        if (!item.clientefacturado.codigo.isNullOrEmpty())
        {
            holder.binding.tvfacturaboleta.setText(item.clientefacturado.documento.substring(0,1))
            holder.binding.tvfacturaboleta.isVisible = true
            holder.binding.ivfacturadivision.isVisible = false
        } else {
            holder.binding.tvfacturaboleta.isVisible = false
            holder.binding.ivfacturadivision.isVisible = true
        }
        if (isCobrarMode){
            holder.binding.ivEditamontodivision.isVisible = false
            holder.binding.llmontodivision.isEnabled = false
            holder.binding.lltransferenciamesa.isEnabled = false
            holder.binding.tvclientedivision.isEnabled = false
            holder.binding.iveliminaclientedivision.isVisible = false

            holder.binding.lldivisionfacturacion.isVisible = false

            holder.binding.llcobrarfacturacion.isVisible = true

            if (item.facturado){
                holder.binding.tvpagaBoletaFactura.setText("✔")
                holder.binding.tvpagaBoletaFactura
                    .setTextColor(ContextCompat.getColor(holder.binding.root.context, R.color.white))
                holder.binding.cvcobrarfacturacion.setCardBackgroundColor(ContextCompat.getColor(holder.binding.root.context, R.color.colorPrimary))
                holder.binding.llcobrarfacturacion.isEnabled = false
            }

        } else {
            holder.binding.ivEditamontodivision.isVisible = true
            holder.binding.llmontodivision.isEnabled = true
            holder.binding.lltransferenciamesa.isEnabled = true
            holder.binding.tvclientedivision.isEnabled = true
            holder.binding.iveliminaclientedivision.isVisible = true

            holder.binding.lldivisionfacturacion.isVisible = true
            holder.binding.llcobrarfacturacion.isVisible = false
        }

        // TextWatcher para la edición del cliente
        holder.binding.tvclientedivision.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    getItem(holder.adapterPosition).clientedivision = s.toString() // Actualizar el texto directamente
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        holder.binding.iveliminaclientedivision.setOnClickListener {
            val pos = holder.absoluteAdapterPosition
            if (pos != RecyclerView.NO_POSITION) { // Verifica que la posición es válida
                val currentList = currentList.toMutableList()
                if (currentList.size>1) {
                    currentList.removeAt(pos)
                }
                currentList.forEach {
                    it.montodivision = it.detalle.sumOf { it -> it.precio * it.cantidad } / currentList.size
                    item.valorasignadodivision = false
                }
                submitList(currentList)
                notifyDataSetChanged()
            }
        }

        holder.binding.llmontodivision.setOnClickListener {
            // Agregar lógica si es necesario
            mItemListener.onItemEditaClick(item)
        }
        holder.binding.lldivisionfacturacion.setOnClickListener {
            // Agregar lógica si es necesario
            mItemListener.onItemEditaClienteClick(item)
        }
        holder.binding.llcobrarfacturacion.setOnClickListener{
            mItemListener.onItemCobrarClick(item)

        }
    }


    inner class ViewHolder(val binding: ItemDivisionBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<PedidoModel>() {
        override fun areItemsTheSame(oldItem: PedidoModel, newItem: PedidoModel): Boolean {
            return oldItem.codigopedido == newItem.codigopedido // Asegúrate de que `id` es único
        }

        override fun areContentsTheSame(oldItem: PedidoModel, newItem: PedidoModel): Boolean {
            return oldItem == newItem // Compara el contenido
        }
    }
}