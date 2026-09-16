package com.example.clinickcheck.data.repository


/**
 * Archivo: com/example/clinickcheck/data/repository/AntecedenteRepository.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.local.entity.AntecedenteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Sugerencia de enfermedad/diagnostico devuelta por la API publica de
 * NIH Clinical Tables, aun no persistida como AntecedenteEntity.
 */
    /**
     * AntecedenteSugerido: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class AntecedenteSugerido(
    val nombreEnfermedad: String,
    val codigoICD10: String?
)

/**
 * Repositorio para la gestión de antecedentes médicos de los pacientes.
 */
    /**
     * AntecedenteRepository: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface AntecedenteRepository {

    /** Obtiene los antecedentes de un paciente de forma reactiva. */
    /**
     * getAntecedentesPorPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getAntecedentesPorPaciente(pacienteId: Long): Flow<List<AntecedenteEntity>>

    /** Obtiene los antecedentes de un paciente una única vez. */
    /**
     * getAntecedentesPorPacienteOnce: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun getAntecedentesPorPacienteOnce(pacienteId: Long): List<AntecedenteEntity>

    /** Registra un nuevo antecedente. */
    /**
     * insertarAntecedente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertarAntecedente(antecedente: AntecedenteEntity): Long

    /** Anula un antecedente registrado por error. */
    /**
     * anularAntecedente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun anularAntecedente(idAntecedente: Long, razon: String)

    /** Verifica la disponibilidad de conexión a internet. */
    /**
     * hayConexionInternet: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun hayConexionInternet(): Boolean

    /**
     * Busca sugerencias de antecedentes en servicios remotos.
     * @return [Result] con la lista de sugerencias o error.
     */
    /**
     * buscarEnApi: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun buscarEnApi(query: String): Result<List<AntecedenteSugerido>>
}

