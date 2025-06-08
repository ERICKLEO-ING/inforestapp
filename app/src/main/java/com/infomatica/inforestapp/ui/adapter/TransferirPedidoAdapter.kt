package com.infomatica.inforestapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.ProductoModel

class TransferirPedidoAdapter(
    private val listaProducto: MutableList<ProductoModel>,
    private val mItemListener: ItemClickListener,
    private  val isEleccion: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = listaProducto.size

    fun getProductos(): List<ProductoModel> = listaProducto

    interface ItemClickListener {
        fun onItemClick(details: ProductoModel?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item_transferir, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val producto = listaProducto[position]
        if (holder is ProductoViewHolder) {
            // Mostrar el nombre del producto
            if (producto.isimpreso){
                if (producto.profundidad ==0){
                    holder.NameTextView.text =  String.format("%.2f", producto.cantidad) + " "+ producto.nombre.replace("||","✓")
                } else {
                    holder.NameTextView.text =  producto.nombre.replace("||","✓")
                }

                holder.checkItemTransferir.isChecked = producto.isSelected
                holder.llSeleccionItem.isVisible = true
            } else {
                holder.llSeleccionItem.isVisible = false
            }

            if (isEleccion == false && producto.isSelected == true){
                if (producto.profundidad >0) {
                    holder.llSeleccionItem.isVisible = false
                } else {
                    holder.llSeleccionItem.isVisible = true
                }
                holder.llSeleccionItem.isEnabled = false
                holder.checkItemTransferir.isVisible = false

                if (producto.subProductos!!.isNotEmpty() && producto.profundidad==0){
                    holder.NameTextView.text = String.format("%.2f", producto.subProductos.filter { it.isSelected }.sumOf { it.cantidad }) + " "+ producto.nombre.replace("||","✓")
                }
            } else{
                holder.llSeleccionItem.isEnabled = true
            }

            if (isEleccion == false && producto.isSelected == false){
                holder.llSeleccionItem.isVisible = false
            }
            // Indentar subitems
            val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.leftMargin = producto.profundidad*70 // Indentación
            holder.itemView.layoutParams = layoutParams

            val clickListener = View.OnClickListener {
                val currentPosition = holder.adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val currentProducto = listaProducto[currentPosition]
                    currentProducto.isSelected = !currentProducto.isSelected

                    if (currentProducto.isSelected) {
                        expandirProducto(currentProducto, currentPosition)
                    } else {
                        colapsarProducto(currentProducto, currentPosition)
                    }
                    notifyItemChanged(currentPosition)
                    // Notificar al listener si es necesario
                    // mItemListener.onItemClick(currentProducto)
                }
            }
            holder.llSeleccionItem.setOnClickListener(clickListener)
            holder.checkItemTransferir.setOnClickListener(clickListener)
        }
    }

    // Función para expandir un producto en subitems
    private fun expandirProducto(producto: ProductoModel, position: Int) {
        producto.subProductos.let { subItems ->
            subItems?.forEach { item ->
                item.isSelected = true
            }
            subItems?.let { listaProducto.addAll(position + 1, it) }
            subItems?.let { notifyItemRangeInserted(position + 1, it.size) }
        }
    }

    // Función para colapsar los subitems de un producto
    private fun colapsarProducto(producto: ProductoModel, position: Int) {
        producto.subProductos.let { subItems ->
            // Colapsar subitems
            subItems?.forEach { item ->
                item.isSelected = false
            }
            subItems?.let { listaProducto.removeAll(it) }
            subItems?.let { notifyItemRangeRemoved(position + 1, it.size) }

            // Verificar si todos los subitems están desmarcados
            val allDeselected = subItems?.all { !it.isSelected }
            if (allDeselected == true) {
                producto.isSelected = false // Desmarcar el producto principal
                notifyItemChanged(position) // Notificar cambio del producto principal
            }
        }
    }


    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val NameTextView: TextView = itemView.findViewById(R.id.tv_producto_itempedido_Transferir)
        val checkItemTransferir: CheckBox = itemView.findViewById(R.id.cb_item_transferir)
        val llSeleccionItem: LinearLayout = itemView.findViewById(R.id.ll_item_eleccion_transferir)
    }
}
