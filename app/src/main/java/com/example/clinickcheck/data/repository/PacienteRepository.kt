package com.example.clinickcheck.data.repository


/**
 * Archivo: com/example/clinickcheck/data/repository/PacienteRepository.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.local.entity.AlertaEntity
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.local.entity.SignoVitalEntity
import com.example.clinickcheck.local.relation.PacienteConHistorias
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para la gestión de datos de pacientes, signos vitales y alertas.
 */
    /**
     * PacienteRepository: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface PacienteRepository {
    
    /** Obtiene todos los pacientes activos. */
    /**
     * getAllPacientesActivos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getAllPacientesActivos(): Flow<List<PacienteEntity>>
    
    /** Busca pacientes activos por una consulta de texto. */
    /**
     * buscarPacientesActivos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun buscarPacientesActivos(query: String): Flow<List<PacienteEntity>>
    
    /** Obtiene un paciente por su ID. */
    /**
     * getPacienteById: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getPacienteById(idPaciente: Long): Flow<PacienteEntity?>
    
    /** Obtiene un paciente con su historial clínico. */
    /**
     * getPacienteConHistorias: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getPacienteConHistorias(idPaciente: Long): Flow<PacienteConHistorias?>

    /** Obtiene los pacientes actualmente hospitalizados. */
    /**
     * getPacientesHospitalizados: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getPacientesHospitalizados(): Flow<List<PacienteEntity>>

    /** Actualiza el estado clínico de un paciente. */
    /**
     * actualizarEstadoClinico: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun actualizarEstadoClinico(idPaciente: Long, nuevoEstado: String, fechaAlta: Long? = null)

    /** Cuenta los pacientes críticos. */
    /**
     * countPacientesCriticos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun countPacientesCriticos(): Flow<Int>
    
    /** Inserta un nuevo paciente. */
    /**
     * insertPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertPaciente(paciente: PacienteEntity): Long
    
    /** Actualiza los datos de un paciente. */
    /**
     * actualizarPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun actualizarPaciente(paciente: PacienteEntity)

    /** Obtiene los signos vitales de un paciente. */
    /**
     * getSignosVitalesByPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getSignosVitalesByPaciente(pacienteId: Long): Flow<List<SignoVitalEntity>>
    
    /** Obtiene los signos vitales de un paciente registrados por un enfermero específico. */
    /**
     * getSignosVitalesByPacienteYEnfermero: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getSignosVitalesByPacienteYEnfermero(pacienteId: Long, enfermeroId: Long): Flow<List<SignoVitalEntity>>
    
    /** Inserta un nuevo registro de signos vitales. */
    /**
     * insertarSignoVital: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertarSignoVital(signoVital: SignoVitalEntity): Long
    
    /** Anula un registro de signos vitales con una razón. */
    /**
     * anularSignoVital: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun anularSignoVital(idSignoVital: Long, razon: String)

    /** Obtiene las alertas no atendidas de un paciente. */
    /**
     * getAlertasNoAtendidasByPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getAlertasNoAtendidasByPaciente(pacienteId: Long): Flow<List<AlertaEntity>>
    
    /** Obtiene los IDs de los pacientes que tienen alertas activas. */
    /**
     * getIdsPacientesConAlertasActivas: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getIdsPacientesConAlertasActivas(): Flow<List<Long>>
    
    /** Obtiene todas las alertas pendientes en el sistema. */
    /**
     * getAlertasPendientes: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getAlertasPendientes(): Flow<List<AlertaEntity>>
    
    /** Inserta una nueva alerta. */
    /**
     * insertarAlerta: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertarAlerta(alerta: AlertaEntity): Long
    
    /** Marca una alerta como atendida. */
    /**
     * marcarAlertaAtendida: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun marcarAlertaAtendida(idAlerta: Long)
}
