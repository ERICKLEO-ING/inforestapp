package com.infomatica.inforestapp.ui.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.ModificadorModel
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.data.model.ToppingModel
import kotlin.math.abs

class ModificadorAdapter(
    private val itemClickListener: ItemClickListener,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listaModificador: List<ModificadorModel> = emptyList()
    var productoModel = ProductoModel(codigo = "")

    companion object {
        const val TYPE_MODIFICADOR = 0
        const val TYPE_TOPPING = 1
    }

    interface ItemClickListener {
        fun onToppingClick(topping: ToppingModel)
        fun onToppingModificadoresClick(topping: ToppingModel)
//        fun onToppingCantidadClick(topping: ToppingModel)
    }

    fun submitList(newList: List<ModificadorModel>, _productoModel:ProductoModel) {
        val diffCallback = ModificadorDiffCallback(listaModificador, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        productoModel = _productoModel
        listaModificador = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        var index = 0
        for (modificador in listaModificador) {
            if (index == position) return TYPE_MODIFICADOR
            index++
            if (modificador.isExpanded) {
                for (topping in modificador.toppings) {
                    if (index == position) return TYPE_TOPPING
                    index++
                }
            }
        }
        return TYPE_MODIFICADOR
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MODIFICADOR -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_modificador, parent, false)
                ModificadorViewHolder(view)
            }
            TYPE_TOPPING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_topping, parent, false)
                ToppingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var count = 0
        for (modificador in listaModificador) {
            if (position == count && holder is ModificadorViewHolder) {
                holder.bind(modificador, itemClickListener, this)
                return
            }
            count++
            if (modificador.isExpanded && position < count + modificador.toppings.size) {
                val toppingIndex = position - count
                val topping = modificador.toppings[toppingIndex]
                if (holder is ToppingViewHolder) {
                    holder.bind(topping, modificador, itemClickListener, this, context)
                }
                return
            }
            if (modificador.isExpanded) {
                count += modificador.toppings.size
            }
        }
    }

    override fun getItemCount(): Int {
        return listaModificador.sumOf { 1 + if (it.isExpanded) it.toppings.size else 0 }
    }

    class ModificadorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreModificador: TextView = view.findViewById(R.id.nombremodificador_text)
        val validacionModificador: TextView = view.findViewById(R.id.tv_validacion_modificador)
        val linearMaximoModificado: LinearLayout = view.findViewById(R.id.ll_maximomodificador)

        val ivAbrirCerrarModificador: ImageView = view.findViewById(R.id.iv_abrir_cerrar_modificador)
        val llModificadorItem: LinearLayout = view.findViewById(R.id.ll_modificador_item)
        val obligaModificador: TextView = view.findViewById(R.id.tv_obligamodificador_text)

        fun bind(modificador: ModificadorModel, clickListener: ItemClickListener, adapter: ModificadorAdapter) {

            nombreModificador.text = modificador.nombre
            linearMaximoModificado.setBackgroundResource(R.drawable.boton_redondo)

            val toppingsSeleccionados = modificador.toppings.filter { it.cantidad>0 }
            var elige = modificador.maximo - toppingsSeleccionados.size

            if (modificador.iscombomodificador) {
                elige = ((modificador.maximo * adapter.productoModel.cantidad) - toppingsSeleccionados.sumOf { it.cantidad }).toInt()
            }

            obligaModificador.apply {
                text = "Opcional"
                setTextColor(ContextCompat.getColor(itemView.context, R.color.colorSecondary))
                if (modificador.isobligatorio) {
                    text = "Obligatorio"
                    setTextColor(ContextCompat.getColor(itemView.context, R.color.lightgray))
                }
            }

            validacionModificador.apply {
                when {
                    elige < 0 && modificador.maximo==1 && modificador.iscombomodificador && !modificador.isnoeliminamodificador -> {
                        text = "Quita ${abs(elige)}"
                        linearMaximoModificado.setBackgroundResource(R.drawable.boton_redondo_rojo)
                        animarMovimiento(linearMaximoModificado, adapter)
                    }
                    elige <= 0 && modificador.hastaCombo==0 -> text = "¡Listo!"
                    modificador.isobligatorio -> {
                        text = if (modificador.iscombomodificador) "Minimo $elige/Elige $elige" else "Minimo 1/Elige $elige"
                        linearMaximoModificado.setBackgroundResource(R.drawable.boton_redondo_rojo)
                        animarMovimiento(linearMaximoModificado, adapter)
                    }
                    else -> {
                        text = if (modificador.iscombomodificador && modificador.hastaCombo > 0) {
                            "Minimo ${modificador.hastaCombo-(adapter.productoModel.cantidad*(adapter.productoModel.cantidadmax-adapter.productoModel.cantidadmin)).toInt()}/Hasta ${modificador.hastaCombo}"
                        } else {
                            "Elige $elige"
                        }
                    }
                }
            }

            //linearMaximoModificado.isVisible = modificador.maximo > 0 || modificador.iscombomodificador && modificador.hastaCombo > 0

            ivAbrirCerrarModificador.rotation = if (modificador.isExpanded) 180f else 0f

            llModificadorItem.setOnClickListener {
                modificador.isExpanded = !modificador.isExpanded
                adapter.notifyDataSetChanged()
            }
        }
        fun animarMovimiento(view: View, adapter: ModificadorAdapter) {
            val animator = ObjectAnimator.ofFloat(view, "translationX", -50f, 50f, 0f, -50f, 50f, 0f)
            animator.duration = 500 // Duración total de la animación
            animator.start()

            // Desplazar RecyclerView hasta este elemento
            // adapter.scrollToPosition(adapterPosition)
        }
    }

    class ToppingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nombreTopping: TextView = view.findViewById(R.id.tv_descripcion_topping)
        private val precioTopping: TextView = view.findViewById(R.id.tv_precio_topping)
        private val cantidadTopping: TextView = view.findViewById(R.id.tv_cantidad_topping)

        private val menosTopping: ImageView = view.findViewById(R.id.iv_menos_topping)
        private val masTopping: ImageView = view.findViewById(R.id.iv_mas_topping)

        private val tercerNivelModificadores: LinearLayout = view.findViewById(R.id.ll_topping_modificador)
        private val descripcionmodificadortoppings: TextView = view.findViewById(R.id.tv_descripcion_modificadortopping)

        fun bind(topping: ToppingModel, modificador: ModificadorModel, clickListener: ItemClickListener,  adapter: ModificadorAdapter, context: Context) {

            // Set the name of the topping
            if (topping.tienepropiedades && topping.cantidad>0.0){
                val nombreSubrayado = SpannableString(topping.nombre).apply {
                    setSpan(UnderlineSpan(), 0, length, 0)
                }
                nombreTopping.text = nombreSubrayado

                topping.descripciontercernivel = topping.modificadores
                    .asSequence() // Convierte la lista en una secuencia para mejorar el rendimiento en grandes volúmenes de datos
                    .mapNotNull { mod ->
                        mod.toppings
                            .asSequence()
                            .filter { top -> top.cantidad > 0.0 }
                            .joinToString(", ") { top -> if (top.iscantidad) { "(${top.cantidad}) ${mod.nombre} ${top.nombre}"} else {"${mod.nombre} ${top.nombre}"} }
                            .takeIf { it.isNotEmpty() } // Evita cadenas vacías
                    }
                    .joinToString(", ")

                if (topping.descripciontercernivel.isNotEmpty()){
                    descripcionmodificadortoppings.isVisible = true
                    descripcionmodificadortoppings.text = topping.descripciontercernivel
                } else   {
                    descripcionmodificadortoppings.isVisible = false
                }
            } else {
                nombreTopping.text = topping.nombre
                descripcionmodificadortoppings.isVisible = false
            }

            if (topping.cantidad > 0){
                // Texto dinámico con subrayado

                var cantidadTop = if (topping.cantidad % 1 == 0.0) {
                    // Si es un número entero
                    topping.cantidad.toInt().toString()
                } else {
                    // Si tiene decimales, muestra como máximo 2
                    String.format("%.2f", topping.cantidad)
                }

//                val cantidadSubrayada = SpannableString(cantidadTop).apply {
//                    setSpan(UnderlineSpan(), 0, length, 0)
//                }
                cantidadTopping.text = cantidadTop
            } else {
                cantidadTopping.text = ""
            }

            // Set the price of the topping, or leave it empty if the price is 0 or less
            if (topping.precio > 0) {
                precioTopping.text = "${topping.moneda}${String.format("%.2f", topping.precio)}"
            } else {
                precioTopping.text = ""
            }

            // Hide quantity and buttons if 'iscantidad' is false
            if (!topping.iscantidad) {
                cantidadTopping.isVisible = false
                menosTopping.isVisible = false
                if (topping.cantidad >0){
                    masTopping.setImageResource(R.drawable.check_activo_18x18)
                }else{
                    masTopping.setImageResource(R.drawable.pedido_mas_36x40)
                }
            } else {
                cantidadTopping.isVisible = true
                masTopping.setImageResource(R.drawable.pedido_mas_36x40)
                if (topping.cantidad > 0){
                    if ( topping.eliminafijotopping) {
                        menosTopping.isVisible = false
                    } else {
                        menosTopping.isVisible = true
                    }
                }else{
                    menosTopping.isVisible = false
                }
            }
            // Disable interactions if the product is printed
            if (adapter.productoModel.isimpreso) {
                masTopping.isEnabled = false
                menosTopping.isEnabled = false
                masTopping.alpha = 0.5f // Optional: Add transparency for visual feedback
                menosTopping.alpha = 0.5f
            } else {
                masTopping.isEnabled = true
                menosTopping.isEnabled = true
                masTopping.alpha = 1f
                menosTopping.alpha = 1f
            }

            fun actualizarCantidadTopping(cantidad: Double, topping: ToppingModel, aumenta : Boolean) {
                if (!adapter.productoModel.isimpreso) {
                    if (cantidad<0 ) return;
                    if (modificador.iscombomodificador) { // COMBOS
                        val cantidadActual = adapter.listaModificador
                            .filter { it.iscombomodificador }
                            .flatMap { it.toppings }
                            .filter { it.cantidad > 0 && it.codigo != topping.codigo }
                            .sumOf { it.cantidad }

                        val cantidadmaximacombo = adapter.productoModel.cantidadmax * adapter.productoModel.cantidad
                        val CantidadMinima = adapter.productoModel.cantidadmin * adapter.productoModel.cantidad
                        if (cantidadmaximacombo < cantidadActual+cantidad && aumenta) {
                            Toast.makeText(context, "Máximo combo: $cantidadmaximacombo", Toast.LENGTH_SHORT).show()
                            return
                        }
                        val toppingsElegidos = modificador.toppings.filter { it.cantidad > 0 }.sumOf { it.cantidad }
                        val canPermitida = modificador.maximo * adapter.productoModel.cantidad
                        if (canPermitida <= toppingsElegidos && modificador.maximo > 0 && aumenta) {
                            Toast.makeText(context, "${modificador.nombre} maximo: $canPermitida", Toast.LENGTH_SHORT).show()
                            return
                        }

//                        if (CantidadMinima > cantidadActual) {
//                            Toast.makeText(context, "Minimo combo: $cantidadmaximacombo", Toast.LENGTH_SHORT).show()
//                            return
//                        }

                    }
                    else { /// PROPIEDADES (Elige la cantidad de toppings seleccionado incluyendose al que se esta seleccionando)
                        if (modificador.maximo==1 && (modificador.toppings.filter { it.iscantidad }
                                .isEmpty())){
                            modificador.toppings.filter { it.codigo != topping.codigo }.forEach { it.cantidad=0.0 }
                        }
                        val cantidadPropiedades = modificador.toppings.filter { it.cantidad > 0 || it.codigo == topping.codigo }.size
                        if ((modificador.maximo < cantidadPropiedades) && modificador.maximo > 0) {
                            Toast.makeText(context, "Maximo de propiedades: ${modificador.maximo} por operador", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                    if (!topping.iscantidad && topping.cantidad>0.0){
                        topping.cantidad = 0.00
                    } else {
                        topping.cantidad = cantidad
                    }

                    if (topping.tienepropiedades && topping.cantidad == 1.0 && topping.descripciontercernivel =="") {
                        clickListener.onToppingModificadoresClick(topping)
                    }

                    clickListener.onToppingClick(topping)
                    return;

                }
            }

            // Asignar el listener
            masTopping.setOnClickListener {
                actualizarCantidadTopping(topping.cantidad +1, topping, true) // Aumentar la cantidad
            }

            menosTopping.setOnClickListener {
                actualizarCantidadTopping(topping.cantidad-1, topping, false) // Disminuir la cantidad
            }

            // Abrir Modal del tercer nivel
            tercerNivelModificadores.setOnClickListener{
                if (topping.tienepropiedades && topping.cantidad>0) {
                    clickListener.onToppingModificadoresClick(topping)
                }
            }
        }

    }

    class ModificadorDiffCallback(
        private val oldList: List<ModificadorModel>,
        private val newList: List<ModificadorModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].codigo == newList[newItemPosition].codigo
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return newList[newItemPosition] // Devuelve solo la información nueva
        }
    }

}
