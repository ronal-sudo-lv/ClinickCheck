package com.example.clinickcheck.data.repository


/**
 * Archivo: com/example/clinickcheck/data/repository/PrescripcionRepository.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.local.entity.DosisRegistroEntity
import com.example.clinickcheck.local.entity.PrescripcionDetalleEntity
import com.example.clinickcheck.local.entity.PrescripcionEntity
import com.example.clinickcheck.local.relation.PrescripcionConDetalles
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para la gestión de prescripciones y administración de dosis.
 */
    /**
     * PrescripcionRepository: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface PrescripcionRepository {
    
    /** Obtiene las prescripciones activas de un paciente. */
    /**
     * getPrescripcionesActivasPorPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getPrescripcionesActivasPorPaciente(idPaciente: Long): Flow<List<PrescripcionConDetalles>>
    
    /** Obtiene todos los detalles de prescripciones activas globalmente. */
    /**
     * getDetallesActivosGlobal: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getDetallesActivosGlobal(): Flow<List<PrescripcionDetalleEntity>>
    
    /** Inserta una nueva prescripción. */
    /**
     * insertPrescripcion: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertPrescripcion(prescripcion: PrescripcionEntity): Long
    
    /** Registra la administración de una dosis. */
    /**
     * registrarDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun registrarDosis(dosis: DosisRegistroEntity): Long
    
    /** Obtiene el historial de dosis para un detalle de prescripción. */
    /**
     * getRegistrosDosisPorDetalle: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getRegistrosDosisPorDetalle(idDetalle: Long): Flow<List<DosisRegistroEntity>>
    
    /** Cuenta las dosis administradas en el día actual. */
    /**
     * countDosisAdministradasHoy: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun countDosisAdministradasHoy(inicioDeHoy: Long): Flow<Int>
    
    /** Anula un registro de dosis con una razón. */
    /**
     * anularDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun anularDosis(idDosis: Long, razon: String)
}
