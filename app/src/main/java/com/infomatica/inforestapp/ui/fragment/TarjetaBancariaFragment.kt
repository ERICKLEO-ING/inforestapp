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
import com.infomatica.inforestapp.data.model.TarjetaBancariaModel
import com.infomatica.inforestapp.databinding.FragmentTarjetasBinding
import com.infomatica.inforestapp.ui.adapter.TarjetaBancariaAdapter


class TarjetaBancariaFragment private constructor(
    private val onResult: (String) -> Unit
) : DialogFragment(), TarjetaBancariaAdapter.ItemClickListener {

    private var _binding: FragmentTarjetasBinding? = null
    private val binding get() = _binding!!
    private lateinit var tarjetaBancariaAdapter: TarjetaBancariaAdapter
    companion object {
        private const val ARG_INITIAL_VALUE = "tarjeta"

        fun newInstance(initialValue: List<TarjetaBancariaModel>, onResult: (String) -> Unit): TarjetaBancariaFragment {
            return TarjetaBancariaFragment(onResult).apply {
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
        _binding = FragmentTarjetasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tarjetasBancarias = arguments?.getParcelableArrayList<TarjetaBancariaModel>(ARG_INITIAL_VALUE) ?: emptyList()

        binding.recyclertarjetasbancarias.setLayoutManager(
            GridLayoutManager(
                requireContext(),
                1,
                RecyclerView.VERTICAL,
                false
            )
        )

        tarjetaBancariaAdapter = TarjetaBancariaAdapter(this)
        binding.recyclertarjetasbancarias.adapter = tarjetaBancariaAdapter
        tarjetaBancariaAdapter.submitList(tarjetasBancarias)

    }
    override fun onItemClick(details: TarjetaBancariaModel?) {
        // Handle the item click event here
        details?.let {
            onResult(it.codigo)
            dismiss()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
