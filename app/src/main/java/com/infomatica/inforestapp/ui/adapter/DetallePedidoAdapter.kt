package com.infomatica.inforestapp.ui.adapter

import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.data.model.SalonModel
import com.infomatica.inforestapp.ui.adapter.ProductoAdapter.ItemClickListener
import java.text.DecimalFormat

class DetallePedidoAdapter(
    private val listaProducto: List<ProductoModel>,
    private val mItemListener: ItemClickListener
) : RecyclerView.Adapter<DetallePedidoAdapter.ViewHolder>() {

    override fun getItemCount(): Int = listaProducto.size

    interface ItemClickListener {
        fun onItemClickProducto(details: ProductoModel?)
        fun onItemClickOrden(details: ProductoModel?)
        fun onItemClickElimina(details: ProductoModel?)
        fun onItemClickEdita(details: ProductoModel?)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_itempedido, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = listaProducto[position]

        var observacion =""
        var propiedades =""
        holder.tvordenitem.text = producto.orden.toString() + "°"

        if (producto.observacion.isNotEmpty()){
            observacion = " ! " + producto.observacion
        }
        if (producto.propiedades.isNotEmpty()){
            propiedades = " * " + producto.propiedades
        }
//        var cantidadprod = if (producto.cantidad % 1 == 0.0) {
//            // Si es un número entero
//            producto.cantidad.toInt().toString()
//        } else {
//            // Si tiene decimales, muestra como máximo 2
//            String.format("%.2f", producto.cantidad)
//        }
        var cantidadprod = DecimalFormat("#.###").format(producto.cantidad)
        holder.NameTextView.text =  cantidadprod + " "+ producto.nombre.replace("||","✓")
        if ((observacion+propiedades).trim().isEmpty()){
            holder.modificadoresTextView.text = ""
            holder.modificadoresTextView.isVisible =false
        } else {
            holder.modificadoresTextView.text = if(observacion.isNotEmpty()){observacion+ System.lineSeparator() + propiedades} else { propiedades }
            holder.modificadoresTextView.isVisible =true
        }

//        holder.CodigoTextView.text = producto.codigo + producto.item
        //holder.CantidadTextView.text =   String.format("%.2f", producto.cantidad)
        if (producto.isimpreso){
            holder.llordenItem.isEnabled = false
            holder.tvordenitem.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.lightgray_600))
            //holder.EditaImageView.isEnabled = false
            holder.EditaImageView.setImageResource(R.drawable.obs_pasivo_24x24)
            holder.EliminarImageView.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.colorText))
            //holder.EditCardView.isVisible=false
        } else {
            holder.EditaImageView.setImageResource(R.drawable.editar_20x20)
            holder.llordenItem.isEnabled = true
        }

        holder.EliminarImageView.setOnClickListener {
            mItemListener.onItemClickElimina(producto)
        }
        holder.EditaImageView.setOnClickListener {
            mItemListener.onItemClickEdita(producto)
        }
        holder.llordenItem.setOnClickListener {
            mItemListener.onItemClickOrden(producto)
        }
        holder.itemView.setOnClickListener {
            mItemListener.onItemClickProducto(producto)
        }
        // Bind other data to the views
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val NameTextView: TextView = itemView.findViewById(R.id.tv_producto_itempedido)
        val  EliminarImageView: ImageView = itemView.findViewById(R.id.iv_elimina_itempedido)
        val  EditaImageView: ImageView = itemView.findViewById(R.id.iv_edita_itempedido)
        val  llordenItem: LinearLayout = itemView.findViewById(R.id.llordenitem)
        val  tvordenitem: TextView = itemView.findViewById(R.id.tvordenitem)
        val modificadoresTextView: TextView = itemView.findViewById(R.id.tv_producto_modificadores)
    }
}