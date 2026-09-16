package com.example.clinickcheck.local.dao


/**
 * Archivo: com/example/clinickcheck/local/dao/UsuarioDao.kt
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
import com.example.clinickcheck.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la gestión de usuarios y autenticación.
 */
@Dao
    /**
     * UsuarioDao: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
interface UsuarioDao {

    /**
     * Registra un nuevo usuario. Se usa REPLACE para permitir re-registros rápidos si es necesario.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /**
     * registrarUsuario: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun registrarUsuario(usuario: UsuarioEntity): Long

    /**
     * Actualiza los datos de un usuario existente.
     */
    @Update
    /**
     * actualizarUsuario: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun actualizarUsuario(usuario: UsuarioEntity)

    /**
     * Busca un usuario por su nombre de usuario (insensible a mayúsculas/minúsculas).
     */
    @Query("SELECT * FROM usuarios WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    /**
     * getUsuarioByUsername: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun getUsuarioByUsername(username: String): UsuarioEntity?

    /**
     * Obtiene un usuario por su ID de forma única (sin Flow).
     */
    @Query("SELECT * FROM usuarios WHERE idUsuario = :idUsuario LIMIT 1")
    /**
     * getUsuarioByIdOnce: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun getUsuarioByIdOnce(idUsuario: Long): UsuarioEntity?

    /**
     * Obtiene un usuario por su ID de forma reactiva (Flow).
     */
    @Query("SELECT * FROM usuarios WHERE idUsuario = :idUsuario LIMIT 1")
    /**
     * getUsuarioById: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getUsuarioById(idUsuario: Long): Flow<UsuarioEntity?>
}

