package com.infomatica.inforestapp.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.infomatica.inforestapp.databinding.ActivityMainBinding
import com.infomatica.inforestapp.ui.viewmodel.QuoteViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Ejemplo de actividad a pantalla completa que muestra y oculta la interfaz del sistema
 * (barra de estado y barra de navegación) mediante la interacción del usuario.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private  val quoteViewmodel: QuoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            quoteViewmodel.onCreate()

            quoteViewmodel.quotaModel.observe(this, Observer { currentCute ->
                binding.prueba1.text = currentCute.author
                binding.prueba2.text = currentCute.quote
            })

            quoteViewmodel.isLoading.observe(this, Observer { x ->
                binding.progressCircular.isVisible = x
            })

            binding.ViewMain.setOnClickListener { quoteViewmodel.randomQuote() }

        } catch (e: Exception) {
            e.printStackTrace() // this view el error en Logcat
        }
    }
}