package com.infomatica.inforestapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.ProductoModel

class DescuentoEleccionAdapter(
    private val listaProducto: MutableList<ProductoModel>,
    private val isEleccion: Boolean
) : RecyclerView.Adapter<DescuentoEleccionAdapter.ProductoViewHolder>() {

    override fun getItemCount(): Int = listaProducto.size

    fun getProductos(): List<ProductoModel> = listaProducto

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item_transferir, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = listaProducto[position]
        val nombreConCheck = producto.nombre.replace("||", "✓")
        val cantidadFormateada = String.format("%.2f", producto.cantidad)
        holder.nameTextView.text = "$cantidadFormateada $nombreConCheck"
        if (producto.lpermitedescuento && producto.descuento<=0) {

            holder.checkItemTransferir.isChecked = producto.isSelected
            holder.llSeleccionItem.isVisible = true
            holder.llSeleccionItem.isEnabled = true
            holder.checkItemTransferir.buttonTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.colorPrimary)
        } else {
            holder.checkItemTransferir.isEnabled = false
            holder.llSeleccionItem.isEnabled = false
            holder.checkItemTransferir.buttonTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.lightgray)
        }

        // Puedes descomentar y usar esta parte si deseas manejar clics sobre el ítem

        val clickListener = View.OnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                val currentProducto = listaProducto[currentPosition]
                currentProducto.isSelected = !currentProducto.isSelected
                notifyItemChanged(currentPosition)
            }
        }
        holder.llSeleccionItem.setOnClickListener(clickListener)
        holder.checkItemTransferir.setOnClickListener(clickListener)

    }

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_producto_itempedido_Transferir)
        val checkItemTransferir: CheckBox = itemView.findViewById(R.id.cb_item_transferir)
        val llSeleccionItem: LinearLayout = itemView.findViewById(R.id.ll_item_eleccion_transferir)
    }
}
