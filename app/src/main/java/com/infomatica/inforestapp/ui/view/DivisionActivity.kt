package com.infomatica.inforestapp.ui.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.databinding.ActivityDivisionBinding
import com.infomatica.inforestapp.ui.BaseActivity
import com.infomatica.inforestapp.ui.adapter.DivisionFacturacionAdapter
import com.infomatica.inforestapp.ui.fragment.DivisionProductoFragment
import com.infomatica.inforestapp.ui.view.VentaActivity.Companion.KEY_RESULT
import com.infomatica.inforestapp.ui.viewmodel.DivisionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DivisionActivity : BaseActivity(), DivisionFacturacionAdapter.ItemClickListener {
    private lateinit var binding: ActivityDivisionBinding
    private lateinit var divisionAdapter: DivisionFacturacionAdapter
    private val divisionViewModel: DivisionViewModel by viewModels()
    private var tipodivision:String = "producto"

    var pedidoClienteSelect : PedidoModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDivisionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configurarOnBackPressed()
        configureRecyclerViews()
        setupObservers()
        setupClickOnListener()

        divisionViewModel.onCreate()

    }
    private fun setupClickOnListener() {
        binding.tvCancelarDivision.setOnClickListener {

            var datos = divisionAdapter.currentList.toMutableList()
            if (datos.any { x -> x.facturado }){
                showToast("No se puede cancelar, hay documentos procesados.")
                return@setOnClickListener
            }

            if ( binding.btnrealizarcobro.text.toString().uppercase()=="FINALIZAR"){

                binding.llagregacliente.isVisible = true
                binding.tabdivisionproducto.isVisible =true
                binding.tabdivisionmonto.isVisible = true

                binding.tvdivisiontitulo.setText("Selecciona los productos correspondientes para cada cliente.")
                binding.btnrealizarcobro.setText("REALIZAR COBRO")
                binding.tvTituloDivision.setText("División de facturación")
                binding.btnrealizarcobro.isEnabled = true
                divisionAdapter.setCobrarMode(false);
            } else {
                finishActivity("")
            }

        }

        binding.llagregacliente.setOnClickListener {
            val pedidocrea = divisionViewModel.resultPedidoModel.value?.copy() ?: return@setOnClickListener // Evita null
            var monto = divisionViewModel.resultPedidoModel.value!!.detalle.sumOf { it -> it.precio * it.cantidad }
//            pedidocrea.codigopedido = "CLIENTE${divisionAdapter.currentList.size+1}"
            pedidocrea.codigopedido = "${pedidocrea.codigopedido}-${divisionAdapter.currentList.size+1}"
            pedidocrea.clientedivision = "CLIENTE${divisionAdapter.currentList.size+1}"
            pedidocrea.observacion += " ${pedidocrea.codigopedido}-${divisionAdapter.currentList.size+1}"
            val det = pedidocrea.detalle.filter { x -> !x.isSelected }.map { it.copy() }.toMutableList()
            det.forEach { x -> x.isSelected = false }
            pedidocrea.detalle= det

            val datos = divisionAdapter.currentList.toMutableList() // Crear una nueva lista mutable
            datos.add(pedidocrea) // Agregar el nuevo pedido
            datos.forEach {
                it.montodivision = monto/datos.size
                it.valorasignadodivision = false
                it.isdivisionmonto = tipodivision == "monto"
            }
            divisionAdapter.submitList(datos) // Enviar la nueva lista al adaptador
            divisionAdapter.notifyDataSetChanged() // Notificar al adaptador que la lista ha cambiado
        }

        binding.tabdivisionproducto.setOnClickListener { selectTab(true) }

        binding.tabdivisionmonto.setOnClickListener { selectTab(false) }

        binding.btnrealizarcobro.setOnClickListener {

            if ( binding.btnrealizarcobro.text.toString().uppercase()=="REALIZAR COBRO"){

                var datos = divisionAdapter.currentList.toMutableList()

                if (datos.any { x -> x.clientefacturado.tidentidad.isEmpty() }){
                    showToast("Debe elegir el documento y cliente a cobrar en cada linea.")
                    return@setOnClickListener
                }

                binding.llagregacliente.isVisible = false
                binding.tabdivisionproducto.isVisible =false
                binding.tabdivisionmonto.isVisible = false

                binding.tvdivisiontitulo.setText("Realiza el cobro de cada uno de los clientes para finalizar el proceso.")
                binding.btnrealizarcobro.setText("FINALIZAR")
                binding.tvTituloDivision.setText("Cobranza dividida")
                binding.btnrealizarcobro.isEnabled = false

                divisionAdapter.setCobrarMode(true);
            } else if ( binding.btnrealizarcobro.text.toString().uppercase()=="FINALIZAR"){
                finishActivity("facturado")
            }
        }
    }

    private fun selectTab(isProductSelected: Boolean) {
        binding.tabdivisionproducto.apply {
            setBackgroundResource(if (isProductSelected) R.drawable.tab_selected else R.drawable.tab_unselected)
            setTextColor(ContextCompat.getColor(context, if (isProductSelected) R.color.colorPrimary else R.color.colorText))
        }

        binding.tabdivisionmonto.apply {
            setBackgroundResource(if (isProductSelected) R.drawable.tab_unselected else R.drawable.tab_selected)
            setTextColor(ContextCompat.getColor(context, if (isProductSelected) R.color.colorText else R.color.colorPrimary))
        }
        if (isProductSelected){
            tipodivision = "producto"
            binding.tvdivisiontitulo.setText("Selecciona los productos correspondientes para cada cliente.")
        } else {
            tipodivision = "monto"
            binding.tvdivisiontitulo.setText("Ingresa los montos correspondientes que pagarán los clientes.")
        }

        val datos = divisionAdapter.currentList.toMutableList() // Crear una nueva lista mutable
        datos.forEach {
            it.isdivisionmonto = !isProductSelected
        }
        divisionAdapter.submitList(datos) // Enviar la nueva lista al adaptador
        divisionAdapter.notifyDataSetChanged() // Notificar al adaptador que la lista ha cambiado
    }
    private fun configureRecyclerViews() {
        binding.recyclerdivision.layoutManager = GridLayoutManager(this, 1, RecyclerView.VERTICAL, false)
        divisionAdapter = DivisionFacturacionAdapter(this)
        binding.recyclerdivision.adapter = divisionAdapter

    }
    private fun setupObservers() {
        divisionViewModel.resultPedidoModel.observe(this, Observer {
            if (it != null) {
                var monto = it.detalle.sumOf { it -> it.precio * it.cantidad }
                it.detalle.forEach { d -> d.isSelected= false }

                val listapedido = mutableListOf<PedidoModel>() // Asegura que es MutableList
                if (it.clientes.isNotEmpty()) {
                    var montodividido = monto / it.clientes.size
                    it.clientes.forEach { cliente ->
                        val pedidocrea = it.copy() // Crea una copia
                        pedidocrea.codigopedido = cliente
                        pedidocrea.clientedivision = cliente
                        val det = pedidocrea.detalle.map { it.copy() }.toMutableList()
                        det.forEach { x -> x.isSelected = false }
                        pedidocrea.detalle= det
                        pedidocrea.montodivision = montodividido
                        listapedido.add(pedidocrea) // Ahora debe funcionar correctamente
                    }
                } else {
                    val pedidocrea = it.copy() // Crea una copia
                    pedidocrea.codigopedido = "CLIENTE"
                    pedidocrea.clientedivision = "CLIENTE"
                    val det = pedidocrea.detalle.map { it.copy() }.toMutableList()
                    det.forEach { x -> x.isSelected = false }
                    pedidocrea.detalle= det
                    pedidocrea.montodivision = monto
                    listapedido.add(pedidocrea) // Ahora debe funcionar correctamente
                }
                divisionAdapter.submitList(listapedido)

                binding.tvmontototaldivision.setText("${it.monedapedido}${String.format("%.2f", monto)}")

            }
        })

        divisionViewModel.resultclienteModel.observe(this, Observer { resultcli ->

            val datos = divisionAdapter.currentList.toMutableList() // Crear una nueva lista mutable
            datos.forEach {
                if (it.codigopedido == pedidoClienteSelect?.codigopedido){
                    if (resultcli != null) {
                        it.clientefacturado = resultcli
                    }
                }
            }
            divisionAdapter.submitList(datos) // Enviar la nueva lista al adaptador
            divisionAdapter.notifyDataSetChanged() // Notificar al adaptador que la lista ha cambiado
        })
    }

    override fun onItemEditaClick(item: PedidoModel?) {
        item ?: return // Evitar null
        pedidoClienteSelect = item
        if (item.isdivisionmonto){
            mostrarNumPadDialog(this.supportFragmentManager) { cantidad ->
                if (cantidad <= 0) {
                    showToast("El valor no puede ser 0.00")
                    return@mostrarNumPadDialog
                }

                val montoTotal = divisionViewModel.resultPedidoModel.value!!.detalle.sumOf { it.precio * it.cantidad }

                if (cantidad > montoTotal) {
                    showToast("El valor no puede ser superior al monto a pagar")
                    return@mostrarNumPadDialog
                }

                // Asignar el monto al item seleccionado sin tocar los demás
                item.montodivision = String.format("%.3f", cantidad).toDouble()
                item.valorasignadodivision = true
                divisionAdapter.notifyDataSetChanged()

                val datos = divisionAdapter.currentList.toMutableList()

                // Calcular lo ya asignado y los ítems sin asignar
                val totalAsignado = datos.filter { it.valorasignadodivision }.sumOf { it.montodivision }
                val itemsSinAsignar = datos.filter { !it.valorasignadodivision }

                var montoRestante = montoTotal - totalAsignado

                if (itemsSinAsignar.isNotEmpty()) {
                    // Distribuir sin que ningún valor sea negativo
                    val montoPorItem = montoRestante / itemsSinAsignar.size
                    itemsSinAsignar.forEach { it.montodivision = maxOf(0.0, String.format("%.3f", montoPorItem).toDouble()) }

                    // Ajustar la diferencia en el último ítem sin asignar
                    val diferencia = montoTotal - datos.sumOf { it.montodivision }
                    if (diferencia != 0.0) {
                        val ultimoItem = itemsSinAsignar.last()
                        val nuevoMonto = maxOf(0.0, ultimoItem.montodivision + diferencia) // Nunca negativo
                        ultimoItem.montodivision = nuevoMonto
                    }
                }

                // Validar que la suma total sea exactamente el monto total
                val nuevaSumaTotal = datos.sumOf { it.montodivision }
                if (nuevaSumaTotal != montoTotal) {
                    showToast("Error: No se logró cuadrar el monto total.")
                    item.montodivision = String.format("%.3f", 0.00).toDouble()
                    item.valorasignadodivision = false
                    return@mostrarNumPadDialog
                }

                // Actualizar la lista
                divisionAdapter.submitList(datos)
                divisionAdapter.notifyDataSetChanged()
            }
        } else {
            // Crear el fragmento y pasarle el objeto PedidoModel y el callback
            val fragment =
                DivisionProductoFragment.newInstance(item) { result ->
                    // Este callback se ejecuta cuando el fragmento devuelve el resultado
                    // Aquí puedes manejar el objeto PedidoModel que has devuelto desde el fragmento
                    //showToast("Resultado recibido: $result")
//                        result.detalle.filter { x-> x.isSelected }.forEach{
//                            showToast("Resultado recibido: ${it.descripcion}")
//                        }

                    val textoConcatenado = result.detalle
                        .filter { it.isSelected }
                        .joinToString(separator = ",") { it.item }

                    // 1) Hacemos copia mutable de la lista original
                    val datos = divisionAdapter.currentList.toMutableList()

                    // 2) Calculamos de antemano el conjunto de items seleccionados
                    val selectedItems = result.detalle
                        .filter { it.isSelected }
                        .map { it.item }
                        .toSet()

                    // 3) Iteramos y actualizamos cada pedido
                    datos.forEach { pedido ->
                        if (pedido.codigopedido == result.codigopedido) {
                            // Reemplazamos detalle y el texto concatenado
                            pedido.detalle = result.detalle.toMutableList()
                            pedido.itemsdivision = textoConcatenado
                            pedido.montodivision = result.detalle.filter { it.isSelected }.sumOf { x -> x.precio * x.cantidad }
                        } else {
                            // Eliminamos de los demás pedidos cualquier detalle cuyo item
                            // esté en el conjunto de selectedItems
                            pedido.detalle.removeAll { detalle ->
                                detalle.item in selectedItems
                            }
                        }
                    }
                    divisionAdapter.submitList(datos) // Enviar la nueva lista al adaptador
                    divisionAdapter.notifyDataSetChanged() // Notificar al adaptador que la lista ha cambiado

                }

            // Mostrar el BottomSheetDialogFragment
            fragment?.show(supportFragmentManager, "DivisionProductoFragment")
        }
    }

    override fun onItemEditaClienteClick(item: PedidoModel?) {
        item ?: return // Evitar null
        pedidoClienteSelect = item
        val intent = Intent(this, ClienteActivity::class.java)
        intent.putExtra("tipo", "division");
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right,R.anim.slide_out_left)
        pedidoClienteLauncher.launch(intent, options)
    }
    override fun onItemCobrarClick(item: PedidoModel?) {
        item ?: return // Evitar null
        pedidoClienteSelect = item
        divisionViewModel.guardarCliente(item.clientefacturado)
        divisionViewModel.guardarPedidoDivision(item)

        val intent = Intent(this, CobroActivity::class.java)
        intent.putExtra("tipo", "division");
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right,R.anim.slide_out_left)
        pedidoClienteLauncher.launch(intent, options)
    }

    private val pedidoClienteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val resultValue = data?.getStringExtra(KEY_RESULT)
                // Verificar si el valor resultante es nulo o vacío, si es necesario
                if (resultValue =="facturado") {
                    val datos = divisionAdapter.currentList.toMutableList() // Crear una nueva lista mutable
                    datos.forEach {
                        if (it.codigopedido == pedidoClienteSelect?.codigopedido){
                            it.facturado = true
                        }
                    }
                    divisionAdapter.submitList(datos) // Enviar la nueva lista al adaptador
                    divisionAdapter.notifyDataSetChanged() // Notificar al adaptador que la lista ha cambiado

                    if (datos.filter { x -> x.facturado == false }.isEmpty()) {
                        binding.btnrealizarcobro.isEnabled =true
                    }
                    //finishActivity("facturado")
                }
                if (resultValue =="ok") {
                   divisionViewModel.consultarClienteRepository()
                }
            }
        }

}