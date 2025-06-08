package com.infomatica.inforestapp.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.data.model.ModificadorModel
import com.infomatica.inforestapp.data.model.ProductoModel
import com.infomatica.inforestapp.data.model.ToppingModel
import com.infomatica.inforestapp.databinding.FragmentTercernivelBinding
import com.infomatica.inforestapp.ui.adapter.ModificadorAdapter

class TercerNivelDialogFragment private constructor(
    private val onResult: (String, MutableList<ModificadorModel>) -> Unit
) : DialogFragment(),ModificadorAdapter.ItemClickListener {

    private var _binding: FragmentTercernivelBinding? = null
    private val binding get() = _binding!!
    private lateinit var modificadorAdapter: ModificadorAdapter

    private var initialValue: String = ""
    private var listModificadores: MutableList<ModificadorModel> = mutableListOf()
    private var prodModel: ProductoModel? = null

    companion object {
        private const val ARG_INITIAL_VALUE = "ARG_INITIAL_VALUE"
        private const val ARG_MODIFICADORES = "ARG_MODIFICADORES"
        private const val ARG_PRODUCTO = "ARG_PRODUCTO"

        fun newInstance(
            initialValue: String,
            listModificadores: MutableList<ModificadorModel>,
            productoModel: ProductoModel,
            onResult: (String, MutableList<ModificadorModel>) -> Unit
        ): TercerNivelDialogFragment {
            return TercerNivelDialogFragment(onResult).apply {
                arguments = Bundle().apply {
                    putString(ARG_INITIAL_VALUE, initialValue)
                    putSerializable(ARG_MODIFICADORES, ArrayList(listModificadores))
                    putSerializable(ARG_PRODUCTO, productoModel)
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            initialValue = it.getString(ARG_INITIAL_VALUE, "")
            listModificadores = (it.getSerializable(ARG_MODIFICADORES) as? ArrayList<ModificadorModel>)?.toMutableList() ?: mutableListOf()
            prodModel = (it.getSerializable(ARG_PRODUCTO) as ProductoModel)
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false) // Evitar cerrar al tocar fuera del modal
        return dialog
    }

    override fun onStart() {
        super.onStart()
        try {
            val dialog = dialog ?: return
            val percent = resources.getFraction(R.fraction.dialog_width_percent, 1, 1) // Obtiene 0.95
            val width = (resources.displayMetrics.widthPixels * percent).toInt()
//        val width = (resources.displayMetrics.widthPixels * 0.95).toInt() // 95% del ancho
            dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            // Hacer que el fondo sea transparente
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (Ex: Exception) {
            println(Ex.message)
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTercernivelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancelar.setOnClickListener {
            dismiss()
        }
        binding.btnAgregar.setOnClickListener {
            val modificadores = modificadorAdapter.listaModificador
            modificadores.forEach { modificador ->
                var toppingsseleccionados = modificador.toppings.sumOf { topping ->
                    topping.cantidad
                }
                if (modificador.isobligatorio && toppingsseleccionados == 0.0){
                    //showToast(modificador.nombre + " es obligatorio!")
                    modificadorAdapter.notifyDataSetChanged()
                    return@setOnClickListener
                }
            }
            dismiss()
        }
        configurarAdapters()
        prodModel?.let { modificadorAdapter.submitList(listModificadores, it) }

        binding.tvTitulo.text = initialValue
    }

    private fun configurarAdapters() {
        binding.rvDetallmodificadores.layoutManager = GridLayoutManager(requireContext(), 1)
        modificadorAdapter = ModificadorAdapter(this,requireContext())
        binding.rvDetallmodificadores.adapter = modificadorAdapter
    }
    override fun onDestroy() {
        super.onDestroy()
        // Llamar a onResult con los valores modificados antes de cerrar el fragmento
        onResult(initialValue, listModificadores)
    }
    override fun onToppingClick(topping: ToppingModel){
        modificadorAdapter.notifyDataSetChanged()
    }

    override fun onToppingModificadoresClick(topping: ToppingModel) {

    }
}
