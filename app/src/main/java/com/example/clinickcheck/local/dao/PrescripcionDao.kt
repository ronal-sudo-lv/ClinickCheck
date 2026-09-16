package com.example.clinickcheck.local.dao


/**
 * Archivo: com/example/clinickcheck/local/dao/PrescripcionDao.kt
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
import androidx.room.Transaction
import androidx.room.Update
import com.example.clinickcheck.local.entity.PrescripcionDetalleEntity
import com.example.clinickcheck.local.entity.PrescripcionEntity
import com.example.clinickcheck.local.relation.DetalleConFarmaco
import com.example.clinickcheck.local.relation.PrescripcionConDetalles
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la gestión de prescripciones médicas.
 */
@Dao
    /**
     * PrescripcionDao: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface PrescripcionDao {

    /**
     * Inserta una nueva prescripción.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * insertPrescripcion: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertPrescripcion(prescripcion: PrescripcionEntity): Long

    /**
     * Inserta una lista de detalles de prescripción.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * insertDetalles: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertDetalles(detalles: List<PrescripcionDetalleEntity>): List<Long>

    /**
     * Actualiza una prescripción existente.
     */
    @Update
    /**
     * updatePrescripcion: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun updatePrescripcion(prescripcion: PrescripcionEntity)

    /**
     * Obtiene todas las prescripciones activas de un paciente, incluyendo sus detalles.
     */
    @Transaction
    @Query("SELECT * FROM prescripcion WHERE idPaciente = :idPaciente AND activa = 1 ORDER BY fechaPrescripcion DESC")
    /**
     * getPrescripcionesActivasByPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getPrescripcionesActivasByPaciente(idPaciente: Long): Flow<List<PrescripcionConDetalles>>

    /**
     * Obtiene los detalles de una prescripción específica junto con el fármaco asociado.
     */
    @Transaction
    @Query("SELECT * FROM prescripcion_detalle WHERE idPrescripcion = :idPrescripcion")
    /**
     * getDetallesConFarmaco: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getDetallesConFarmaco(idPrescripcion: Long): Flow<List<DetalleConFarmaco>>

    /**
     * Obtiene todos los detalles de prescripciones activas de todos los pacientes no eliminados.
     */
    @Query("""
        SELECT pd.* FROM prescripcion_detalle pd
        INNER JOIN prescripcion p ON pd.idPrescripcion = p.idPrescripcion
        INNER JOIN paciente pa ON p.idPaciente = pa.idPaciente
        WHERE p.activa = 1 AND pa.eliminadoEn IS NULL
    """)
    /**
     * getDetallesActivosGlobal: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getDetallesActivosGlobal(): Flow<List<PrescripcionDetalleEntity>>
}
