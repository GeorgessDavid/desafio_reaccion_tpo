package com.georgessdavid.desafioreaccionapp

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.media.AudioManager
import android.media.ToneGenerator
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.*

class MainActivity : AppCompatActivity() {

    // --- ESTADOS Y VARIABLES DE CONFIGURACIÓN ---
    private enum class GameState { CONFIG, ESPERANDO, ESTIMULO, RESULTADO_PARCIAL, FINAL }
    private var estadoActual = GameState.CONFIG

    private var version = "1.0.19";
    private var nombreJugador = ""
    private var dificultad = "Fácil"
    private var iteracionesTotales = 20
    private var tiempoMaximoMs = 20000L
    private var modoEntrenamiento = false

    // --- VARIABLES DE JUEGO ---
    private var nivelActual = 1
    private var iteracionActual = 0
    private var tiempoInicial: Long = 0
    private var tiemposReaccion = mutableListOf<Long>()
    private var esEstimuloValido = true // Si el usuario DEBE tocar o NO

    private val handler = Handler(Looper.getMainLooper())
    private var runnableEstimulo: Runnable? = null
    private var runnableTimeout: Runnable? = null
    private val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)

    // --- COMPONENTES UI ---
    private lateinit var layoutMain: ConstraintLayout
    private lateinit var layoutConfig: View
    private lateinit var layoutJuego: View
    private lateinit var layoutResultados: View

    private lateinit var editNombre: EditText
    private lateinit var spinnerDificultad: Spinner
    private lateinit var editIteraciones: EditText
    private lateinit var editTiempoMax: EditText
    private lateinit var txtEstimulo: TextView
    private lateinit var txtInfoProgreso: TextView
    private lateinit var txtInstruccion: TextView
    private lateinit var txtStatsFinales: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vincularVistas()
        configurarSpinner()

        findViewById<Button>(R.id.btnComenzar).setOnClickListener { iniciarJuego() }
        findViewById<Button>(R.id.btnReiniciar).setOnClickListener { mostrarConfiguracion() }
        
        layoutMain.setOnClickListener { manejarToquePantalla() }
    }

    private fun vincularVistas() {
        layoutMain = findViewById(R.id.mainLayout)
        layoutConfig = findViewById(R.id.layoutConfig)
        layoutJuego = findViewById(R.id.layoutJuego)
        layoutResultados = findViewById(R.id.layoutResultados)

        editNombre = findViewById(R.id.editNombre)
        spinnerDificultad = findViewById(R.id.spinnerDificultad)
        editIteraciones = findViewById(R.id.editIteraciones)
        editTiempoMax = findViewById(R.id.editTiempoMax)

        txtEstimulo = findViewById(R.id.txtEstimulo)
        txtInfoProgreso = findViewById(R.id.txtInfoProgreso)
        txtInstruccion = findViewById(R.id.txtInstruccion)
        txtStatsFinales = findViewById(R.id.txtStatsFinales)

        findViewById<TextView>(R.id.txtVersion).text = "v$version"
    }

    private fun configurarSpinner() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.dificultades, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDificultad.adapter = adapter

        spinnerDificultad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = parent?.getItemAtPosition(position).toString()
                // Tiempos por defecto según enunciado
                when (selected) {
                    "Entrenamiento" -> { editTiempoMax.setText("30"); modoEntrenamiento = true }
                    "Fácil" -> { editTiempoMax.setText("20"); modoEntrenamiento = false }
                    "Medio" -> { editTiempoMax.setText("15"); modoEntrenamiento = false }
                    "Difícil" -> { editTiempoMax.setText("10"); modoEntrenamiento = false }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun mostrarConfiguracion() {
        estadoActual = GameState.CONFIG
        layoutConfig.visibility = View.VISIBLE
        layoutJuego.visibility = View.GONE
        layoutResultados.visibility = View.GONE
        layoutMain.setBackgroundColor(Color.WHITE)
    }

    private fun iniciarJuego() {
        nombreJugador = editNombre.text.toString().ifEmpty { "Jugador 1" }
        dificultad = spinnerDificultad.selectedItem.toString()
        iteracionesTotales = editIteraciones.text.toString().toIntOrNull() ?: 20
        
        val tMaxSec = editTiempoMax.text.toString().toIntOrNull() ?: 20
        tiempoMaximoMs = (tMaxSec.coerceIn(1, 30) * 1000).toLong()

        nivelActual = 1
        iteracionActual = 0
        tiemposReaccion.clear()

        layoutConfig.visibility = View.GONE
        layoutJuego.visibility = View.VISIBLE
        
        proximaIteracion()
    }

    private fun proximaIteracion() {
        if (iteracionActual >= iteracionesTotales) {
            if (nivelActual < 3) {
                nivelActual++
                iteracionActual = 0
                Toast.makeText(this, "¡Subiste al Nivel $nivelActual!", Toast.LENGTH_SHORT).show()
                proximaIteracion()
                return
            } else {
                finalizarJuego()
                return
            }
        }

        iteracionActual++
        actualizarUIProgreso()
        
        estadoActual = GameState.ESPERANDO
        layoutMain.setBackgroundColor(Color.DKGRAY)
        txtEstimulo.setTextColor(Color.WHITE)
        txtInstruccion.setTextColor(Color.WHITE)
        txtInfoProgreso.setTextColor(Color.WHITE)
        txtEstimulo.text = ""
        txtInstruccion.text = "¡Preparate!"

        val delayAleatorio = Random().nextInt(2001) + 1500L // 1.5 a 3.5 seg
        runnableEstimulo = Runnable { lanzarEstimulo() }
        handler.postDelayed(runnableEstimulo!!, delayAleatorio)
    }

    private fun lanzarEstimulo() {
        estadoActual = GameState.ESTIMULO
        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        
        // Decidir si es un estímulo al que hay que reaccionar o no (atención inversa)
        esEstimuloValido = Random().nextBoolean()
        
        // Tipo de estímulo: 0=Color, 1=Palabra, 2=Número
        val tipo = Random().nextInt(3)
        generarVisual(tipo, esEstimuloValido)

        tiempoInicial = System.currentTimeMillis()

        // Si el usuario NO debe tocar, esperamos el tiempo máximo para darlo por bueno
        if (!esEstimuloValido) {
            runnableTimeout = Runnable { 
                if (estadoActual == GameState.ESTIMULO) {
                    registrarAciertoNoAction() 
                }
            }
            handler.postDelayed(runnableTimeout!!, 2000) // 2 segundos de espera para no-go
        } else {
            // Si debe tocar, ponemos el timeout del enunciado
            runnableTimeout = Runnable {
                if (estadoActual == GameState.ESTIMULO) {
                    perderPorTiempo()
                }
            }
            handler.postDelayed(runnableTimeout!!, tiempoMaximoMs)
        }
    }

    private fun generarVisual(tipo: Int, valido: Boolean) {
        when (tipo) {
            0 -> { // COLOR
                if (valido) {
                    layoutMain.setBackgroundColor(Color.GREEN)
                    txtEstimulo.setTextColor(Color.BLACK)
                    txtInstruccion.setTextColor(Color.BLACK)
                    txtEstimulo.text = "VERDE"
                    txtInstruccion.text = "¡TOCA!"
                } else {
                    layoutMain.setBackgroundColor(Color.RED)
                    txtEstimulo.setTextColor(Color.WHITE)
                    txtInstruccion.setTextColor(Color.WHITE)
                    txtEstimulo.text = "ROJO"
                    txtInstruccion.text = "¡NO TOQUES!"
                }
            }
            1 -> { // PALABRA
                layoutMain.setBackgroundColor(Color.WHITE)
                txtEstimulo.setTextColor(Color.BLACK)
                txtInstruccion.setTextColor(Color.BLACK)
                if (valido) {
                    txtEstimulo.text = "AHORA"
                    txtInstruccion.text = "¡TOCA!"
                } else {
                    txtEstimulo.text = "ESPERA"
                    txtInstruccion.text = "¡NO TOQUES!"
                }
            }
            2 -> { // NÚMERO
                layoutMain.setBackgroundColor(Color.WHITE)
                txtEstimulo.setTextColor(Color.BLACK)
                txtInstruccion.setTextColor(Color.BLACK)
                
                // Elegir aleatoriamente entre regla de PARIDAD o rule de PRIMOS
                val esReglaPrimo = Random().nextBoolean()
                
                if (esReglaPrimo) {
                    val num = generarNumeroCondicion(valido, true)
                    txtEstimulo.text = num.toString()
                    txtInstruccion.text = if (valido) "¡Toca si NO ES PRIMO!" else "¡No toques si ES PRIMO!"
                } else {
                    val num = generarNumeroCondicion(valido, false)
                    txtEstimulo.text = num.toString()
                    txtInstruccion.text = if (valido) "¡Toca si es PAR!" else "¡No toques si es IMPAR!"
                }
            }
        }
    }

    private fun generarNumeroCondicion(valido: Boolean, reglaPrimo: Boolean): Int {
        var n: Int
        do {
            n = Random().nextInt(151) // Rango 0-150
            val cumpleCondicion = if (reglaPrimo) !esPrimo(n) else n % 2 == 0
            // Si buscamos uno VALIDO, debe cumplir la condicion. 
            // Si buscamos uno INVALIDO, no debe cumplirla.
        } while (cumpleCondicion != valido)
        return n
    }

    private fun esPrimo(n: Int): Boolean {
        if (n <= 1) return false
        for (i in 2..Math.sqrt(n.toDouble()).toInt()) {
            if (n % i == 0) return false
        }
        return true
    }

    private fun manejarToquePantalla() {
        when (estadoActual) {
            GameState.ESPERANDO -> {
                handler.removeCallbacks(runnableEstimulo!!)
                perderJuego("¡Tocaste demasiado pronto!")
            }
            GameState.ESTIMULO -> {
                handler.removeCallbacks(runnableTimeout!!)
                if (esEstimuloValido) {
                    registrarReaccion()
                } else {
                    perderJuego("¡No debías tocar!")
                }
            }
            GameState.RESULTADO_PARCIAL -> {
                proximaIteracion()
            }
            else -> {}
        }
    }

    private fun registrarReaccion() {
        val tiempo = System.currentTimeMillis() - tiempoInicial
        tiemposReaccion.add(tiempo)
        
        estadoActual = GameState.RESULTADO_PARCIAL
        layoutMain.setBackgroundColor(Color.LTGRAY)
        txtEstimulo.setTextColor(Color.BLACK)
        txtInstruccion.setTextColor(Color.BLACK)
        txtInfoProgreso.setTextColor(Color.BLACK)
        txtEstimulo.text = "$tiempo ms"
        txtInstruccion.text = "¡Bien! Toca para el siguiente."
    }

    private fun registrarAciertoNoAction() {
        estadoActual = GameState.RESULTADO_PARCIAL
        layoutMain.setBackgroundColor(Color.LTGRAY)
        txtEstimulo.setTextColor(Color.BLACK)
        txtInstruccion.setTextColor(Color.BLACK)
        txtInfoProgreso.setTextColor(Color.BLACK)
        txtEstimulo.text = "¡CORRECTO!"
        txtInstruccion.text = "No tocaste. ¡Bien! Toca para seguir."
    }

    private fun perderPorTiempo() {
        perderJuego("¡Se acabó el tiempo!")
    }

    private fun perderJuego(motivo: String) {
        toneGen.startTone(ToneGenerator.TONE_SUP_ERROR, 500)
        estadoActual = GameState.FINAL
        layoutJuego.visibility = View.GONE
        layoutResultados.visibility = View.VISIBLE
        layoutMain.setBackgroundColor(Color.BLACK)
        
        txtStatsFinales.setTextColor(Color.WHITE)
        txtStatsFinales.text = "PERDISTE\n$motivo\n\nJugador: $nombreJugador\nIntentos: ${tiemposReaccion.size}"
    }

    private fun finalizarJuego() {
        estadoActual = GameState.FINAL
        layoutJuego.visibility = View.GONE
        layoutResultados.visibility = View.VISIBLE
        
        val promedio = if (tiemposReaccion.isNotEmpty()) tiemposReaccion.average().toInt() else 0
        val mejor = if (tiemposReaccion.isNotEmpty()) tiemposReaccion.minOrNull() else 0

        txtStatsFinales.text = "¡FELICITACIONES!\nCompletaste todos los niveles.\n\n" +
                "Jugador: $nombreJugador\n" +
                "Dificultad: $dificultad\n" +
                "Promedio: $promedio ms\n" +
                "Mejor tiempo: $mejor ms"
        
        guardarRecord(mejor ?: 0)
    }

    private fun guardarRecord(tiempo: Long) {
        if (modoEntrenamiento || tiempo <= 0) return
        
        val prefs = getSharedPreferences("RecordsApp", MODE_PRIVATE)
        val mejorActual = prefs.getLong("mejor_$nombreJugador", Long.MAX_VALUE)
        
        if (tiempo < mejorActual) {
            prefs.edit().putLong("mejor_$nombreJugador", tiempo).apply()
        }
    }

    private fun actualizarUIProgreso() {
        txtInfoProgreso.text = "Nivel $nivelActual - Intento $iteracionActual/$iteracionesTotales"
    }
}
