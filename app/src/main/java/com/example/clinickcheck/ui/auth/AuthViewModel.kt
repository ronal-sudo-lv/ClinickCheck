package com.example.clinickcheck.ui.auth


/**
 * Archivo: com/example/clinickcheck/ui/auth/AuthViewModel.kt
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
import com.example.clinickcheck.SecurityUtils
import com.example.clinickcheck.local.dao.UsuarioDao
import com.example.clinickcheck.local.entity.TurnoTrabajo
import com.example.clinickcheck.local.entity.UsuarioEntity
import com.example.clinickcheck.local.preferences.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Representa los diferentes estados de la autenticación.
 */
sealed class AuthState {
    /**
     * Idle: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    object Idle : AuthState()
    /**
     * Loading: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    object Loading : AuthState()
    /**
     * Success: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    object Success : AuthState()
    /**
     * Error: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    data class Error(val message: String) : AuthState()
}

/**
 * ViewModel encargado del flujo de autenticación (Login, Registro, Logout).
 */
@HiltViewModel
    /**
     * AuthViewModel: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class AuthViewModel @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     */
    /**
     * login: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun login(username: String, password: String) {
        val normalizedUsername = username.trim()
        val normalizedPassword = password.trim()

        if (normalizedUsername.isBlank() || normalizedPassword.isBlank()) {
            _authState.value = AuthState.Error("Por favor completa todos los campos.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val usuario = usuarioDao.getUsuarioByUsername(normalizedUsername)

            if (usuario == null) {
                _authState.value = AuthState.Error("Usuario no encontrado.")
                return@launch
            }

            val isValid = SecurityUtils.verifyPassword(normalizedPassword, usuario.salt, usuario.passwordHash)

            if (isValid) {
                sessionManager.saveSession(
                    userId = usuario.idUsuario,
                    username = usuario.username,
                    role = usuario.rol
                )
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Contraseña incorrecta.")
            }
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     */
    /**
     * registrar: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun registrar(
        username: String,
        password: String,
        nombre: String,
        apellido: String,
        rol: String = "ENFERMERO",
        turno: TurnoTrabajo = TurnoTrabajo.MANANA,
        numLicencia: String? = null
    ) {
        val normalizedUsername = username.trim()
        val normalizedPassword = password.trim()
        val normalizedNombre = nombre.trim()
        val normalizedApellido = apellido.trim()

        if (normalizedUsername.isBlank() || normalizedPassword.isBlank() ||
            normalizedNombre.isBlank() || normalizedApellido.isBlank()
        ) {
            _authState.value = AuthState.Error("Todos los campos son obligatorios.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val existente = usuarioDao.getUsuarioByUsername(normalizedUsername)
            if (existente != null) {
                _authState.value = AuthState.Error("El usuario ya existe.")
                return@launch
            }

            val salt = SecurityUtils.generateSalt()
            val hash = SecurityUtils.hashPassword(normalizedPassword, salt)

            val nuevoUsuario = UsuarioEntity(
                username = normalizedUsername,
                passwordHash = hash,
                salt = salt,
                nombre = normalizedNombre,
                apellido = normalizedApellido,
                rol = rol,
                numLicencia = numLicencia?.trim()?.takeIf { it.isNotBlank() },
                turno = turno
            )

            val userId = usuarioDao.registrarUsuario(nuevoUsuario)
            sessionManager.saveSession(
                userId = userId,
                username = normalizedUsername,
                role = nuevoUsuario.rol
            )
            _authState.value = AuthState.Success
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    /**
     * logout: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _authState.value = AuthState.Idle
        }
    }

    /**
     * Reinicia el estado de autenticación a Idle.
     */
    /**
     * resetState: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
