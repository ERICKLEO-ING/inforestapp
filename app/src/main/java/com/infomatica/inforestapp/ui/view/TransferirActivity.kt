package com.infomatica.inforestapp.ui.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.MesasModel
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.data.model.SalonModel
import com.infomatica.inforestapp.databinding.ActivityTransferirBinding
import com.infomatica.inforestapp.ui.BaseActivity
import com.infomatica.inforestapp.ui.adapter.DescuentoEleccionAdapter
import com.infomatica.inforestapp.ui.adapter.MesaTransferirAdapter
import com.infomatica.inforestapp.ui.adapter.SalonSpinnerAdapter
import com.infomatica.inforestapp.ui.adapter.TransferirPedidoAdapter
import com.infomatica.inforestapp.ui.view.VentaActivity.Companion.KEY_RESULT
import com.infomatica.inforestapp.ui.viewmodel.TransferirViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferirActivity : BaseActivity(), TransferirPedidoAdapter.ItemClickListener,MesaTransferirAdapter.ItemClickListener {
    private lateinit var binding: ActivityTransferirBinding
    private val transferirViewModel: TransferirViewModel by viewModels()
    private lateinit var transferirAdapter: TransferirPedidoAdapter

    private lateinit var descuentoEleccionAdapter: DescuentoEleccionAdapter

    private var isBottomSheetMesaOpen = false
    private lateinit var mesaATransferir: MesasModel
    private var tipoUso: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tipoUso = intent.getStringExtra("tipo") ?: ""

        transferirViewModel.onCreate()

        binding.tvCancelarTransferencia.setOnClickListener{

            if (binding.tvCancelarTransferencia.text == "VOLVER") {
                generarTransferenciaMesa(false)
            } else {
                finishActivity("")
            }
        }
        binding.rvDetallepedidoTransferir.setLayoutManager(
            GridLayoutManager(
                applicationContext,
                1,
                RecyclerView.VERTICAL,
                false
            )
        )
//        binding.tvCreateOrder.setOnClickListener{
//            crearOrdenSinMesa(true);
//        }
//        binding.ivAnulaSinmesa.setOnClickListener{
//            crearOrdenSinMesa(false);
//        }
        transferirViewModel.resultPedidoModel.observe(this, Observer {

            if (it?.status == 100){
                it.detalle.forEach { item ->
                    item.isSelected = false
                    item.cantidadtransferir = 0.0
                }
                if (tipoUso=="descuento") {
                    binding.tvCodigomesaTransferir.setText("Descuentos")
                    binding.tvTituloTransferirDescripcion.setText("Elige 1 o más productos.")
                    binding.btnElegirMesaTransferencia.setText("CONTINUAR")
                } else {
                    binding.tvCodigomesaTransferir.text = it.nombremesa
                }

                if (tipoUso=="descuento") {
                    val productosRaiz = it.detalle.filter { it.profundidad == 0 }.toMutableList()

                    descuentoEleccionAdapter = DescuentoEleccionAdapter(
                        productosRaiz,
                        true
                    )
                    binding.rvDetallepedidoTransferir.adapter = descuentoEleccionAdapter
                } else {
                    val datos  = expandirEnSubitems(it.detalle.toList())

                    transferirAdapter = TransferirPedidoAdapter(
                        datos.toMutableList(),
                        this,
                        true
                    )
                    binding.rvDetallepedidoTransferir.adapter = transferirAdapter
                }

            }else if (it?.status == 0) {
                showToast("Transferencia completada!")
                finish()
            } else {
                if (it != null) {
                    showToast(it.mensaje)
                }
            }
        })
        transferirViewModel.listaSalon.observe(this, Observer { salons ->
            if (salons.isNullOrEmpty()) {
                showToast("No hay salones disponibles.")
                return@Observer
            }

            val salonSpinnerAdapter = SalonSpinnerAdapter(this, salons)
            binding.spinnerSalones.adapter = salonSpinnerAdapter

            // Función para filtrar mesas
            fun filtrarMesas(salon: SalonModel) {
                // Limpiar las mesas anteriores antes de agregar las nuevas
                val mesasFiltradas = salon.mesas.filter { mesa ->
                    mesa.estado != "01" &&
                            mesa.nombre != binding.tvCodigomesaTransferir.text &&
                            mesa.codigopedido.isNotEmpty() &&
                            (if (transferirViewModel.resultusuario.vercuentas > 0) {
                                mesa.codigopedido != ""
                            } else {
                                mesa.codigopedido.contains(transferirViewModel.resultusuario.usuario)
                            })
                }

                // Limpiar la lista de mesas anteriores
                val mesaTransferirAdapter = MesaTransferirAdapter(this)
                binding.rvDetallepedidoTransferir.adapter = mesaTransferirAdapter

                if (mesasFiltradas.isNotEmpty()) {
                    mesaTransferirAdapter.submitList(mesasFiltradas)
                } else {
                    showToast("Sin registros de mesas o pedidos.")
                }
            }

            // Filtrar las mesas del primer salón o el primero que tenga mesas
            val salonConMesas = salons.firstOrNull { salon ->
                salon.mesas.any { mesa ->
                    mesa.estado != "01" &&
                            mesa.nombre != binding.tvCodigomesaTransferir.text &&
                            mesa.codigopedido.isNotEmpty() &&
                            (if (transferirViewModel.resultusuario.vercuentas > 0) {
                                mesa.codigopedido != ""
                            } else {
                                mesa.codigopedido.contains(transferirViewModel.resultusuario.usuario)
                            })
                }
            }

            if (salonConMesas != null) {
                filtrarMesas(salonConMesas)
                binding.spinnerSalones.setSelection(salons.indexOf(salonConMesas))
            } else {
                showToast("Sin registros de mesas o pedidos en todos los salones.")
                // Limpiar la lista de mesas si no se encuentran mesas
                filtrarMesas(salonConMesas ?: salons.first()) // Si no se encuentra, muestra el primer salón disponible
            }

            // Configurar el listener para detectar cambios de selección en el spinner
            binding.spinnerSalones.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    binding.btnElegirMesaTransferencia.isEnabled = false
                    filtrarMesas(salons[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        })

        binding.btnElegirMesaTransferencia.setOnClickListener{

            if (tipoUso=="descuento") {

                val productos = descuentoEleccionAdapter.getProductos()
                if (productos.count { it.isSelected } == 0) {
                    showToast("No hay registros seleccionados.")
                    return@setOnClickListener;
                }

                val intent = Intent(this, DescuentoActivity::class.java)

                val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
                pedidoLauncher.launch(intent, options)

                return@setOnClickListener
            }

            if (binding.btnElegirMesaTransferencia.text == "TRANSFERIR PEDIDO") {
                val productos = transferirAdapter.getProductos()
                if (productos.count { it.isSelected } == 0) {
                    showToast("No hay registros seleccionados para la transferencia.")
                    return@setOnClickListener;
                }

                ModalPassTransFerencia(
                    transferirViewModel.resultusuario.lpasswordtransferencia
                ) { password ->
                    productos.forEach { item ->
                        if (item.cantidad>1) {
                            item.cantidadtransferir = item.subProductos!!.filter { it.isSelected
                            }.sumOf { it.cantidad }
                        } else {
                            item.cantidadtransferir = item.cantidad
                        }
                    }

                    // Maneja el valor del password aquí
                    //showToast("El password ingresado es: $password")

                    val productosselect =  productos.filter {
                        it.isSelected && it.profundidad == 0
                    }

                    if (mesaATransferir.codigopedido.trim().isNotEmpty()){
                        transferirViewModel.transferirItemsPedidos(
                            productosselect,
                            mesaATransferir.codigopedido.substring(0,10),
                            "",password)
                    } else {
//                        if (binding.tvTransfernciaObservacion.text.trim().isNotEmpty()){
//                            transferirViewModel.transferirItemsPedidos(
//                                productosselect,
//                                "",
//                                "".toString(),password)
//                        } else{
//                            showToast("Pedido sin mesa debe tener una observación.")
//                        }
                        showToast("No hay mesa seleccionada.")
                    }
                }
            }
            else {
                generarTransferenciaMesa(true)
                transferirViewModel.fetchSalonMesasList()
            }
        }

//        binding.rlTransferenciaMesa.setOnClickListener(){
//          transferirViewModel.fetchSalonMesasList()
//        }
    }
    override fun onItemClick(details: ProductoModel?) {
        // Handle the item click event here
        details?.let {
        }
    }

    override fun onItemClick(details: MesasModel?) {
        // Handle the item click event here
        details?.let {
            mesaATransferir = it
            binding.btnElegirMesaTransferencia.isEnabled = true
        }
    }

//    fun crearOrdenSinMesa(valor: Boolean){
//        binding.rlTransferenciaMesa.isEnabled = !valor
//        if (!valor) {
//            binding.rlTransferenciaMesa.setBackgroundResource(0) // Quita el fondo
//        } else {
//            binding.rlTransferenciaMesa.setBackgroundColor(Color.parseColor("#E2E5E8")) // Aplica un color
//        }
//
//
//        binding.rlTransferenciaObservacion.isVisible = valor
//        if (valor){
//            binding.tvTransfernciaObservacion.setText("${binding.tvCodigomesaTransferir.text} - 0001")
//        } else {
//            binding.tvTransfernciaObservacion.setText("")
//        }
//        binding.tvMesaElegida.text = "Eliga la mesa o pedido de destino"
//        mesaATransferir = MesasModel("","")
//        binding.ivAnulaSinmesa.isVisible = valor
//        binding.btnElegirMesaTransferencia.isEnabled = valor
//    }

    fun generarTransferenciaMesa (valor : Boolean){

        val productos = transferirAdapter.getProductos()
        if (productos.count { it.isSelected } == 0 &&  binding.btnElegirMesaTransferencia.text == "ELEGIR MESA") {
            showToast("No hay registros seleccionados para la transferencia.")
            return;
            // Código a ejecutar si hay al menos un producto seleccionado
        }

        transferirAdapter = TransferirPedidoAdapter(
            productos.toMutableList(),
            this,
            !valor)

        binding.rvDetallepedidoTransferir.adapter = transferirAdapter
        binding.rvDetallepedidoTransferir.isEnabled = !valor
        //binding.llElegirMesaTransferir.isVisible = valor
        binding.btnElegirMesaTransferencia.isEnabled = !valor
        binding.llsalonetransferencia.isVisible = valor

        if (valor){
            binding.rvDetallepedidoTransferir.setLayoutManager(
                GridLayoutManager(
                    applicationContext,
                    2,
                    RecyclerView.VERTICAL,
                    false
                )
            )
            val mesaTransferirAdapter = MesaTransferirAdapter(this)
            binding.rvDetallepedidoTransferir.adapter = mesaTransferirAdapter
            mesaTransferirAdapter.submitList(emptyList())

            binding.tvTituloTransferirDescripcion.text = "Revisa y elige el destino"
            binding.btnElegirMesaTransferencia.text = "TRANSFERIR PEDIDO"
            binding.tvCancelarTransferencia.text = "VOLVER"

        } else {
            binding.rvDetallepedidoTransferir.setLayoutManager(
                GridLayoutManager(
                    applicationContext,
                    1,
                    RecyclerView.VERTICAL,
                    false
                )
            )
            binding.tvTituloTransferirDescripcion.text = "Selecciona los productos a transferir"
            binding.btnElegirMesaTransferencia.text = "ELEGIR MESA"
            binding.tvCancelarTransferencia.text = "CANCELAR"
            //binding.tvMesaElegida.text = "Eliga la mesa o pedido de destino"
            //crearOrdenSinMesa(false)
            binding.btnElegirMesaTransferencia.isEnabled = true
        }
    }

    private fun showBottomSheetMesaDialog(mesasModel: List<MesasModel>) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_listamesa, null)
        bottomSheetDialog.setContentView(view)

        //isBottomSheetMesaOpen = true
        configurarBottomSheetMesa(view, mesasModel, bottomSheetDialog)
        //bottomSheetDialog.setCancelable(false)
        // Set the flag to true when the dialog is shown
        isBottomSheetMesaOpen = true

        // Reset the flag when the dialog is dismissed
        bottomSheetDialog.setOnDismissListener {
            isBottomSheetMesaOpen = false
        }
        bottomSheetDialog.show()
    }

    private fun configurarBottomSheetMesa(view: View, mesasModel: List<MesasModel>, bottomSheetDialog: BottomSheetDialog) {
        try {
            // Configuración del ListView con MesasModel
            val listView = view.findViewById<ListView>(R.id.listView_mesas)

            // Adaptador personalizado para mostrar solo los nombres de las mesas
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                mesasModel.map { "${it.codigopedido.substring(0,10)} - ${it.nombre} "  } // Solo pasamos los nombres al adaptador
            )
            listView.adapter = adapter

            // Evento de clic para cada elemento
            listView.setOnItemClickListener { _, _, position, _ ->
                val mesaSeleccionada = mesasModel[position]
                mesaATransferir = mesaSeleccionada
                //binding.tvMesaElegida.text = "${mesaSeleccionada.nombre}"

                binding.btnElegirMesaTransferencia.isEnabled = true

                //showToast("Seleccionaste: ${mesaSeleccionada.nombre} ")
                bottomSheetDialog.dismiss() // Cerrar el BottomSheet al seleccionar
                isBottomSheetMesaOpen = false
            }

        } catch (ex: Exception) {
            showToast(ex.message.toString())
        }
    }
    private fun ModalPassTransFerencia(shouldOpenModal: Boolean, onPasswordEntered: (String) -> Unit) {
        if (!shouldOpenModal) {
            // Si no se debe abrir el modal, simplemente retorna
            onPasswordEntered("PASAR")
            return
        }

        // Crear un LinearLayout para contener el EditText
        val linearLayout = LinearLayout(this@TransferirActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50)
        }

        // Crear el campo de contraseña
        val passwordEditText = EditText(this@TransferirActivity).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Ingrese su password"
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            isFocusable = false
            isFocusableInTouchMode=false
        }

        // Agregar el OnClickListener para el passwordEditText
        passwordEditText.setOnClickListener {
            mostrarNumPadDialog(this.supportFragmentManager,"login"){ cantidad ->
                var enterocantidad = cantidad.toInt()
                passwordEditText.setText(enterocantidad.toString())
            }
        }

        linearLayout.addView(passwordEditText)

        // Construir el AlertDialog
        AlertDialog.Builder(this@TransferirActivity)
            .setTitle("INFOREST APP")
            .setMessage("Ingresar password de supervisor.")
            .setView(linearLayout)
            .setCancelable(false)
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss() // Dismiss dialog on cancel
            }
            .setPositiveButton("Confirmar") { dialog, _ ->
                val password = passwordEditText.text.toString().trim()

                if (password.isEmpty()) {
                    showToast("Password obligatorio!")
                } else {
                    onPasswordEntered(password) // Llamar al callback con el valor del password
                    dialog.dismiss()
                }
            }
            .show()
    }

    fun expandirEnSubitems(listaProducto: List<ProductoModel>): List<ProductoModel> {
        val listaConSubitems = mutableListOf<ProductoModel>()
        for (producto in listaProducto) {
            if (producto.cantidad > 1 && producto.cantidad % 1 == 0.0) {
                // Si la cantidad es un entero mayor a 1, expandir
                val subProductos = mutableListOf<ProductoModel>()

                val cantidadEntera = producto.cantidad.toInt()

                // Agregar los productos completos por la parte entera
                repeat(cantidadEntera) {
                    subProductos.add(
                        producto.copy(
                            cantidad = 1.0,
                            nombre = "${producto.nombre.substringBefore(" - ")}",
                            subProductos = mutableListOf(),
                            profundidad = 1
                        )
                    )
                }

                listaConSubitems.add(producto.copy(subProductos = subProductos))
            } else {
                // Si la cantidad no es mayor a 1 o es decimal, no expandir
                listaConSubitems.add(producto.copy(subProductos = mutableListOf()))
            }
        }
        return listaConSubitems
    }

    private val pedidoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val resultValue = data?.getStringExtra(KEY_RESULT)
                // Verificar si el valor resultante es nulo o vacío, si es necesario

                if (resultValue =="okdescuento") {
                    finishActivity("okdescuento")
                }
            }
        }
}