package com.example.clinickcheck.local.dao


/**
 * Archivo: com/example/clinickcheck/local/dao/PersonalDao.kt
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
import com.example.clinickcheck.local.entity.PersonalEntity
import com.example.clinickcheck.local.entity.RolPersonal
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la gestión del personal de salud.
 */
@Dao
    /**
     * PersonalDao: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface PersonalDao {

    /**
     * Inserta un nuevo miembro del personal. Si ya existe, lo reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * insertPersonal: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun insertPersonal(personal: PersonalEntity): Long

    /**
     * Actualiza la información de un miembro del personal.
     */
    @Update
    /**
     * updatePersonal: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun updatePersonal(personal: PersonalEntity)

    /**
     * Obtiene todo el personal activo, ordenado por apellidos y nombres.
     */
    @Query("SELECT * FROM personal WHERE activo = 1 ORDER BY apellido, nombre ASC")
    /**
     * getAllPersonalActivo: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getAllPersonalActivo(): Flow<List<PersonalEntity>>

    /**
     * Obtiene el personal activo filtrado por su rol.
     */
    @Query("SELECT * FROM personal WHERE rol = :rol AND activo = 1")
    /**
     * getPersonalByRol: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getPersonalByRol(rol: RolPersonal): Flow<List<PersonalEntity>>

    /**
     * Obtiene un miembro del personal por su ID.
     */
    @Query("SELECT * FROM personal WHERE idPersonal = :idPersonal")
    /**
     * getPersonalById: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun getPersonalById(idPersonal: Long): PersonalEntity?
}
