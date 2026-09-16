package com.example.clinickcheck.local.preferences


/**
 * Archivo: com/example/clinickcheck/local/preferences/UserSessionManager.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_session")

/**
 * Gestor de la sesión del usuario utilizando DataStore.
 * Almacena información persistente sobre el estado de la autenticación.
 */
@Singleton
    /**
     * UserSessionManager: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class UserSessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_ID = longPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val USER_ROLE = stringPreferencesKey("user_role")
    }

    /** Flujo que indica si el usuario ha iniciado sesión. */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN] ?: false
    }

    /** Flujo con el ID del usuario actual. */
    val userId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID]
    }

    /** Flujo con el nombre de usuario actual. */
    val username: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USERNAME]
    }

    /** Flujo con el rol del usuario actual. */
    val userRole: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_ROLE]
    }

    /**
     * Guarda los datos de la sesión tras un login exitoso.
     */
    /**
     * saveSession: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun saveSession(userId: Long = -1L, username: String, role: String) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[USER_ID] = userId
            prefs[USERNAME] = username
            prefs[USER_ROLE] = role
        }
    }

    /**
     * Actualiza el rol del usuario.
     */
    /**
     * updateUserRole: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun updateUserRole(role: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ROLE] = role
        }
    }

    /**
     * Limpia todos los datos de la sesión actual.
     */
    /**
     * clearSession: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
