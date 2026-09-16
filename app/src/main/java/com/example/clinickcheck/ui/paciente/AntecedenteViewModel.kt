package com.example.clinickcheck.ui.paciente


/**
 * Archivo: com/example/clinickcheck/ui/paciente/AntecedenteViewModel.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clinickcheck.local.entity.AntecedenteEntity
import com.example.clinickcheck.local.preferences.UserSessionManager
import com.example.clinickcheck.data.repository.AntecedenteRepository
import com.example.clinickcheck.data.repository.AntecedenteSugerido
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
    /**
     * AntecedenteViewModel: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class AntecedenteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val antecedenteRepository: AntecedenteRepository,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val pacienteId: Long = checkNotNull(savedStateHandle["pacienteId"])

    val antecedentes: StateFlow<List<AntecedenteEntity>> =
        antecedenteRepository.getAntecedentesPorPaciente(pacienteId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _terminoBusqueda = MutableStateFlow("")
    val terminoBusqueda: StateFlow<String> = _terminoBusqueda.asStateFlow()

    private val _sugerencias = MutableStateFlow<List<AntecedenteSugerido>>(emptyList())
    val sugerencias: StateFlow<List<AntecedenteSugerido>> = _sugerencias.asStateFlow()

    private val _buscando = MutableStateFlow(false)
    val buscando: StateFlow<Boolean> = _buscando.asStateFlow()

    // true cuando la ultima busqueda fallo por falta de conexion: activa el
    // flujo de ingreso manual con el indicador "Modo Offline".
    private val _modoOffline = MutableStateFlow(!antecedenteRepository.hayConexionInternet())
    val modoOffline: StateFlow<Boolean> = _modoOffline.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    /**
     * onTerminoBusquedaChanged: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun onTerminoBusquedaChanged(nuevoTermino: String) {
        _terminoBusqueda.value = nuevoTermino
    }

    /**
     * buscarEnApi: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun buscarEnApi() {
        val termino = _terminoBusqueda.value
        if (termino.isBlank()) {
            _sugerencias.value = emptyList()
            return
        }
        viewModelScope.launch {
            _buscando.value = true
            _mensajeError.value = null
            val resultado = antecedenteRepository.buscarEnApi(termino)
            resultado
                .onSuccess { lista ->
                    _sugerencias.value = lista
                    _modoOffline.value = false
                    if (lista.isEmpty()) {
                        _mensajeError.value = "Sin resultados para \"$termino\". Puedes agregar el antecedente manualmente."
                    }
                }
                .onFailure { error ->
                    _sugerencias.value = emptyList()
                    _modoOffline.value = !antecedenteRepository.hayConexionInternet()
                    _mensajeError.value = if (_modoOffline.value) {
                        "Sin conexión a internet. Puedes agregar el antecedente manualmente."
                    } else {
                        "No se pudo consultar la API: ${error.message ?: "error desconocido"}"
                    }
                }
            _buscando.value = false
        }
    }

    /**
     * agregarDesdeApi: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun agregarDesdeApi(sugerido: AntecedenteSugerido) {
        viewModelScope.launch {
            val enfermeroId = userSessionManager.userId.first()
            antecedenteRepository.insertarAntecedente(
                AntecedenteEntity(
                    pacienteId = pacienteId,
                    nombreEnfermedad = sugerido.nombreEnfermedad,
                    codigoICD10 = sugerido.codigoICD10,
                    origen = "API",
                    registradoPor = enfermeroId
                )
            )
            _sugerencias.value = emptyList()
            _terminoBusqueda.value = ""
        }
    }

    /**
     * agregarManual: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun agregarManual(
        nombreEnfermedad: String,
        codigoICD10: String?,
        descripcion: String?,
        esRelevanteSignosVitales: Boolean
    ) {
        if (nombreEnfermedad.isBlank()) return
        viewModelScope.launch {
            val enfermeroId = userSessionManager.userId.first()
            antecedenteRepository.insertarAntecedente(
                AntecedenteEntity(
                    pacienteId = pacienteId,
                    nombreEnfermedad = nombreEnfermedad.trim(),
                    codigoICD10 = codigoICD10?.trim()?.takeIf { it.isNotBlank() },
                    descripcion = descripcion?.trim()?.takeIf { it.isNotBlank() },
                    esRelevanteSignosVitales = esRelevanteSignosVitales,
                    origen = "MANUAL",
                    registradoPor = enfermeroId
                )
            )
        }
    }

    /**
     * Anula un antecedente ingresado por error. No se borra: queda visible
     * en la lista (atenuado/tachado) junto con la razon de anulacion.
     */
    /**
     * anularAntecedente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun anularAntecedente(antecedente: AntecedenteEntity, razon: String) {
        if (razon.isBlank()) return
        viewModelScope.launch {
            antecedenteRepository.anularAntecedente(antecedente.idAntecedente, razon.trim())
        }
    }

    /**
     * refrescarConexion: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun refrescarConexion() {
        _modoOffline.value = !antecedenteRepository.hayConexionInternet()
    }
}
