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
import com.infomatica.inforestapp.data.model.ImpresoraCajaModel
import com.infomatica.inforestapp.databinding.FragmentImpresoracajaBinding
import com.infomatica.inforestapp.ui.adapter.ImpresoraCajaAdapter


class ImpresoraCajaFragment private constructor(
    private val onResult: (ImpresoraCajaModel) -> Unit
) : DialogFragment(), ImpresoraCajaAdapter.ItemClickListener {

    private var _binding: FragmentImpresoracajaBinding? = null
    private val binding get() = _binding!!
    private lateinit var impresoraCajaAdapter: ImpresoraCajaAdapter
    private var selectImpresoraCaja: ImpresoraCajaModel? = null

    companion object {
        private const val ARG_INITIAL_VALUE = "impresora"

        fun newInstance(initialValue: List<ImpresoraCajaModel>, onResult: (ImpresoraCajaModel) -> Unit): ImpresoraCajaFragment {
            return ImpresoraCajaFragment(onResult).apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_INITIAL_VALUE, ArrayList(initialValue))
                }
            }
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false) // Desactivar el cierre al tocar fuera del modal
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        val percent = resources.getFraction(R.fraction.dialog_width_percent, 1, 1) // Obtiene 0.95
        val width = (resources.displayMetrics.widthPixels * percent).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        // Hacer que el fondo sea transparente
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImpresoracajaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listDatos = arguments?.getParcelableArrayList<ImpresoraCajaModel>(ARG_INITIAL_VALUE) ?: emptyList()

        binding.recyclerimpresoras.setLayoutManager(
            GridLayoutManager(
                requireContext(),
                1,
                RecyclerView.VERTICAL,
                false
            )
        )

        impresoraCajaAdapter = ImpresoraCajaAdapter(this)
        binding.recyclerimpresoras.adapter = impresoraCajaAdapter
        impresoraCajaAdapter.submitList(listDatos)
        binding.btncancelarimpresora.setOnClickListener {
            onResult(ImpresoraCajaModel(codigo = "", descripcion = ""))
            dismiss()
        }
        binding.btnaceptarimpresora.setOnClickListener {
            selectImpresoraCaja?.let { it1 -> onResult(it1) }
            dismiss()
        }

    }
    override fun onItemClick(details: ImpresoraCajaModel?) {
        // Handle the item click event here
        details?.let {
            selectImpresoraCaja = it
            binding.btnaceptarimpresora.isEnabled = true
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}