package com.example.clinickcheck.ui.paciente


/**
 * Archivo: com/example/clinickcheck/ui/paciente/PacienteViewModel.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clinickcheck.local.entity.EstadoPaciente
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.data.repository.PacienteRepository
import com.example.clinickcheck.usecase.paciente.GetPacientesActivosUseCase
import com.example.clinickcheck.usecase.paciente.RegistrarPacienteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Representa una fila de la lista de pacientes, ya enriquecida con la
 * información de si tiene o no alertas críticas pendientes de atender.
 */
    /**
     * PacienteListItem: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class PacienteListItem(
    val paciente: PacienteEntity,
    val tieneAlertaActiva: Boolean
)

/**
 * ViewModel encargado de la gestión de la lista de pacientes y el dashboard.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
    /**
     * PacienteViewModel: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class PacienteViewModel @Inject constructor(
    private val pacienteRepository: PacienteRepository,
    private val getPacientesActivosUseCase: GetPacientesActivosUseCase,
    private val registrarPacienteUseCase: RegistrarPacienteUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _estadoFiltro = MutableStateFlow<EstadoPaciente?>(null)
    val estadoFiltro: StateFlow<EstadoPaciente?> = _estadoFiltro.asStateFlow()

    private val pacientesBase: Flow<List<PacienteEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                getPacientesActivosUseCase()
            } else {
                pacienteRepository.buscarPacientesActivos(query)
            }
        }

    private val idsConAlertaActiva: Flow<Set<Long>> =
        pacienteRepository.getIdsPacientesConAlertasActivas()
            .map { it.toSet() }

    /** Lista de pacientes filtrada y enriquecida con estado de alertas. */
    val pacientes: StateFlow<List<PacienteListItem>> = combine(
        pacientesBase,
        idsConAlertaActiva,
        _estadoFiltro
    ) { pacientesLista, idsConAlerta, estado ->
        pacientesLista
            .filter { paciente ->
                estado == null || paciente.estado.equals(estado.name, ignoreCase = true)
            }
            .map { paciente ->
                PacienteListItem(
                    paciente = paciente,
                    tieneAlertaActiva = idsConAlerta.contains(paciente.idPaciente)
                )
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /** Total de pacientes registrados. */
    val totalPacientes: StateFlow<Int> = getPacientesActivosUseCase()
        .map { it.size }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = 0)

    /** Número de pacientes en estado crítico. */
    val pacientesCriticosCount: StateFlow<Int> = getPacientesActivosUseCase()
        .map { lista -> lista.count { it.estado.equals(EstadoPaciente.CRITICO.name, ignoreCase = true) } }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = 0)

    /** Número de alertas pendientes en el sistema. */
    val alertasPendientesCount: StateFlow<Int> = pacienteRepository.getAlertasPendientes()
        .map { it.size }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = 0)

    /** Actualiza el término de búsqueda. */
    /**
     * onSearchQueryChanged: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    /** Actualiza el filtro por estado del paciente. */
    /**
     * onEstadoFiltroChanged: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun onEstadoFiltroChanged(estado: EstadoPaciente?) {
        _estadoFiltro.value = estado
    }

    /** Registra un nuevo paciente en el sistema. */
    /**
     * registrarPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun registrarPaciente(
        nombre: String,
        apellido: String,
        dni: String,
        cama: String,
        fechaNacimiento: Long,
        sexo: String,
        estado: EstadoPaciente,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val nuevoPaciente = PacienteEntity(
                nombre = nombre,
                apellido = apellido,
                dni = dni,
                cama = cama,
                fechaNacimiento = fechaNacimiento,
                sexo = sexo,
                estado = estado.name
            )
            val result = registrarPacienteUseCase(nuevoPaciente)
            if (result.isSuccess) {
                onResult(true, "Paciente registrado correctamente")
            } else {
                onResult(false, "Error al registrar paciente: ${result.exceptionOrNull()?.message}")
            }
        }
    }
}

