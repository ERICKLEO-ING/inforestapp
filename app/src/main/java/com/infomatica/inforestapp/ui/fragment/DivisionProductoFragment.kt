package com.infomatica.inforestapp.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.infomatica.inforestapp.data.model.PedidoModel
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.databinding.FragmentDivisionproductosBinding
import com.infomatica.inforestapp.ui.adapter.DivisionProductoAdapter

class DivisionProductoFragment : BottomSheetDialogFragment(), DivisionProductoAdapter.ItemClickListener {

    private var _binding: FragmentDivisionproductosBinding? = null
    private val binding get() = _binding!!
    private var pedido: PedidoModel? = null
    private lateinit var divisionProductoAdapter: DivisionProductoAdapter
    // Interfaz de callback para devolver el resultado al fragmento
    private var onResult: (PedidoModel) -> Unit = {}

    companion object {
        private const val ARG_INITIAL_VALUE = "Pedido"

        // Método estático para crear una nueva instancia del fragmento
        fun newInstance(initialValue: PedidoModel, onResult: (PedidoModel) -> Unit): DivisionProductoFragment {
            return DivisionProductoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_INITIAL_VALUE, initialValue)
                }
                this.onResult = onResult
            }
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // grab the super dialog and cast
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet = (dialogInterface as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                // force it open full-height
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                // avoid “cut off” peek-height
                behavior.peekHeight = sheet.height
            }
        }

        return dialog
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDivisionproductosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar el objeto PedidoModel desde los argumentos
        pedido = arguments?.getSerializable(ARG_INITIAL_VALUE) as? PedidoModel

        // Aquí puedes usar el objeto `pedido` y trabajar con él
        pedido?.let {
            binding.tvTitulodivisionproductos.setText(it.clientedivision)
            // Ejemplo: establecer datos en la vista o realizar alguna acción con el objeto
        }

        // Manejo de los botones
        binding.btnCancelardivisionproductos.setOnClickListener {
            dismiss() // Cerrar el fragmento
        }

        binding.btnAgregardivisionproductos.setOnClickListener {
            pedido?.let {
                onResult(it)  // Llamar al callback con el objeto PedidoModel
            }
            dismiss() // Cerrar el fragmento
        }

        binding.recyclerdivisionproductos.setLayoutManager(
            GridLayoutManager(
                requireContext(),
                1,
                RecyclerView.VERTICAL,
                false
            )
        )
        //val datos  = expandirEnSubitems(pedido!!.detalle)

        divisionProductoAdapter = DivisionProductoAdapter(
            pedido!!.detalle,//datos.toMutableList(),
            this, true)
        binding.recyclerdivisionproductos.adapter = divisionProductoAdapter
    }
    fun expandirEnSubitems(listaProducto: List<ProductoModel>): List<ProductoModel> {
        val listaConSubitems = mutableListOf<ProductoModel>()
        for (producto in listaProducto) {
            if (producto.cantidad > 1) {
                val subProductos = mutableListOf<ProductoModel>()

                repeat(producto.cantidad.toInt()) {
                    subProductos.add(
                        producto.copy(
                            cantidad = 1.0,
                            nombre = "${producto.nombre.substringBefore(" - ")}",
                            subProductos = mutableListOf(), // Asegurar subProductos no nulo
                            profundidad = 1
                        )
                    )
                }

                listaConSubitems.add(producto.copy(subProductos = subProductos))
            } else {
                listaConSubitems.add(producto.copy(subProductos = mutableListOf())) // Asegurar no nulo
            }
        }
        return listaConSubitems
    }
    override fun onItemClick(details: ProductoModel?) {
        // Manejar el evento de clic del elemento aquí
        details?.let {
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // El comportamiento de cancelación al tocar fuera ya lo maneja BottomSheetDialogFragment por defecto
}
