package com.example.clinickcheck.local.dao


/**
 * Archivo: com/example/clinickcheck/local/dao/AlertaDao.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.clinickcheck.local.entity.AlertaEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la gestión de alertas de pacientes.
 */
@Dao
    /**
     * AlertaDao: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface AlertaDao {

    /**
     * Inserta una nueva alerta. Si ya existe, la reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * insertarAlerta: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertarAlerta(alerta: AlertaEntity): Long

    /**
     * Obtiene todas las alertas de un paciente específico, ordenadas por fecha de creación descendente.
     */
    @Query("SELECT * FROM alerta WHERE pacienteId = :pacienteId ORDER BY fechaCreacion DESC")
    /**
     * getAlertasPorPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getAlertasPorPaciente(pacienteId: Long): Flow<List<AlertaEntity>>

    /**
     * Obtiene las alertas no atendidas de un paciente específico.
     */
    @Query("SELECT * FROM alerta WHERE pacienteId = :pacienteId AND atendida = 0 ORDER BY fechaCreacion DESC")
    /**
     * getAlertasNoAtendidasByPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getAlertasNoAtendidasByPaciente(pacienteId: Long): Flow<List<AlertaEntity>>

    /**
     * Obtiene todas las alertas pendientes (no atendidas) en el sistema.
     */
    @Query("SELECT * FROM alerta WHERE atendida = 0 ORDER BY fechaCreacion DESC")
    /**
     * getAlertasPendientes: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getAlertasPendientes(): Flow<List<AlertaEntity>>

    /**
     * Obtiene los IDs únicos de los pacientes que tienen alertas activas.
     */
    @Query("SELECT DISTINCT pacienteId FROM alerta WHERE atendida = 0")
    /**
     * getIdsPacientesConAlertasActivas: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getIdsPacientesConAlertasActivas(): Flow<List<Long>>

    /**
     * Marca una alerta como atendida.
     */
    @Query("UPDATE alerta SET atendida = 1 WHERE idAlerta = :idAlerta")
    /**
     * marcarComoAtendida: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun marcarComoAtendida(idAlerta: Long)
}
