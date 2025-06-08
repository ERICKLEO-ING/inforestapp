package com.infomatica.inforestapp.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.infomatica.inforestapp.R
import com.infomatica.inforestapp.databinding.FragmentObservacionBinding

class ObservacionDialogFragment private constructor(
    private val onResult: (String) -> Unit
) : DialogFragment() {

    private var _binding: FragmentObservacionBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_INITIAL_VALUE = ""

        fun newInstance(initialValue: String, onResult: (String) -> Unit): ObservacionDialogFragment {
            return ObservacionDialogFragment(onResult).apply {
                arguments = Bundle().apply {
                    putString(ARG_INITIAL_VALUE, initialValue)
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
        _binding = FragmentObservacionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var observaciontexto = arguments?.getString(ARG_INITIAL_VALUE) ?: ""

        binding.etObservacion.setText(observaciontexto)
        binding.btnCancelar.setOnClickListener {
            onResult("")
            dismiss()
        }
        binding.btnAceptar.setOnClickListener{
            observaciontexto = binding.etObservacion.text.toString()
            onResult(observaciontexto)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
