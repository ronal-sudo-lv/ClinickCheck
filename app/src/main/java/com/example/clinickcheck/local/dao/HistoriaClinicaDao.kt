package com.example.clinickcheck.local.dao


/**
 * Archivo: com/example/clinickcheck/local/dao/HistoriaClinicaDao.kt
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
import androidx.room.Update
import com.example.clinickcheck.local.entity.HistoriaClinicaEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la gestión de historias clínicas.
 */
@Dao
    /**
     * HistoriaClinicaDao: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface HistoriaClinicaDao {

    /**
     * Inserta un nuevo registro en la historia clínica.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * insertHistoria: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertHistoria(historia: HistoriaClinicaEntity): Long

    /**
     * Actualiza un registro existente en la historia clínica.
     */
    @Update
    /**
     * updateHistoria: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun updateHistoria(historia: HistoriaClinicaEntity)

    /**
     * Obtiene todas las historias clínicas de un paciente, ordenadas por fecha de registro descendente.
     */
    @Query("SELECT * FROM historia_clinica WHERE idPaciente = :idPaciente ORDER BY fechaRegistro DESC")
    /**
     * getHistoriasByPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getHistoriasByPaciente(idPaciente: Long): Flow<List<HistoriaClinicaEntity>>
}
