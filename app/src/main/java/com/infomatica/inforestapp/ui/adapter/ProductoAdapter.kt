package com.infomatica.inforestapp.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.ProductoModel

class ProductoAdapter(
    private val listaProducto: List<ProductoModel>,
    private val mItemListener: ItemClickListener
) : RecyclerView.Adapter<ProductoAdapter.ViewHolder>() {

    override fun getItemCount(): Int = listaProducto.size

    interface ItemClickListener {
        fun onItemClick(details: ProductoModel?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_producto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = listaProducto[position]
        holder.NameTextView.text = producto.nombre
        holder.codigoTextView.text = producto.codigo
        holder.precioTextView.text =  producto.moneda  + String.format("%.2f", producto.precio)

       // if (producto.iscombo){
       //     holder.iscomboTextView.isVisible = true
       // }else {
       //     holder.iscomboTextView.isVisible = false
       // }
        if (producto.lcontrolcritico && producto.stockcritico>0){
            holder.tvPlatoCritico.isVisible = true
            holder.tvPlatoCritico.text = producto.stockcritico.toInt().toString()
        } else  {
            holder.tvPlatoCritico.isVisible = false
        }
        if (producto.lcontrolcritico && producto.stockcritico<=0.0){
            holder.itemView.isEnabled =false
            holder.llproductoView.setBackgroundResource(R.color.ColorNeutral300)
            //holder.anuncioTextView.isVisible =true
            holder.NameTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.Colorfullinactivo))
            holder.precioTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.Colorfullinactivo))
        } else {
            holder.anuncioTextView.isVisible =false
        }
        holder.itemView.setOnClickListener {
            mItemListener.onItemClick(producto)
        }

        // Bind other data to the views
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val NameTextView: TextView = itemView.findViewById(R.id.tv_nombreproducto)
        val codigoTextView: TextView = itemView.findViewById(R.id.tv_codigoproducto)
        val precioTextView: TextView = itemView.findViewById(R.id.tv_precioproducto)
        val anuncioTextView: TextView = itemView.findViewById(R.id.tvanuncio)
        val iscomboTextView: TextView = itemView.findViewById(R.id.tv_producto_is_combo)
        val llproductoView: LinearLayout = itemView.findViewById(R.id.llproducto)
        val tvPlatoCritico: TextView = itemView.findViewById(R.id.tvPlatoCritico)
    }
}