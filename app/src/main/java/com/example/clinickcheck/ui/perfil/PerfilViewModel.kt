package com.example.clinickcheck.ui.perfil


/**
 * Archivo: com/example/clinickcheck/ui/perfil/PerfilViewModel.kt
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
import com.example.clinickcheck.local.dao.DosisRegistroDao
import com.example.clinickcheck.local.dao.SignoVitalDao
import com.example.clinickcheck.local.dao.UsuarioDao
import com.example.clinickcheck.local.entity.TurnoTrabajo
import com.example.clinickcheck.local.entity.UsuarioEntity
import com.example.clinickcheck.local.preferences.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PerfilUiState {
    /**
     * Cargando: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    object Cargando : PerfilUiState()
    /**
     * Listo: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    data class Listo(val usuario: UsuarioEntity) : PerfilUiState()
    /**
     * Error: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    data class Error(val mensaje: String) : PerfilUiState()
}

/** Contadores del "Resumen del turno" mostrados en tarjetas compactas. */
    /**
     * ResumenTurno: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
data class ResumenTurno(
    val pacientesAtendidos: Int = 0,
    val signosVitalesRegistrados: Int = 0,
    val dosisAdministradas: Int = 0
)

@HiltViewModel
    /**
     * PerfilViewModel: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class PerfilViewModel @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val signoVitalDao: SignoVitalDao,
    private val dosisRegistroDao: DosisRegistroDao,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<PerfilUiState>(PerfilUiState.Cargando)
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val _guardadoExitoso = MutableStateFlow(false)
    val guardadoExitoso: StateFlow<Boolean> = _guardadoExitoso.asStateFlow()

    private val _resumenTurno = MutableStateFlow(ResumenTurno())
    val resumenTurno: StateFlow<ResumenTurno> = _resumenTurno.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _cerrandoSesion = MutableStateFlow(false)
    val cerrandoSesion: StateFlow<Boolean> = _cerrandoSesion.asStateFlow()

    init {
        cargarUsuarioActivo()
    }

    /**
     * cargarUsuarioActivo: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun cargarUsuarioActivo() {
        viewModelScope.launch {
            _uiState.value = PerfilUiState.Cargando
            val userId = sessionManager.userId.first()
            if (userId == null) {
                _uiState.value = PerfilUiState.Error("No hay una sesión activa.")
                return@launch
            }
            val usuario = usuarioDao.getUsuarioByIdOnce(userId)
            if (usuario == null) {
                _uiState.value = PerfilUiState.Error("No se encontró el usuario en la base de datos.")
            } else {
                _uiState.value = PerfilUiState.Listo(usuario)
                cargarResumenTurno(userId)
            }
        }
    }

    /**
     * cargarResumenTurno: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private suspend fun cargarResumenTurno(enfermeroId: Long) {
        _resumenTurno.value = ResumenTurno(
            pacientesAtendidos = signoVitalDao.countPacientesDistintosPorEnfermero(enfermeroId),
            signosVitalesRegistrados = signoVitalDao.countPorEnfermero(enfermeroId),
            dosisAdministradas = dosisRegistroDao.countPorEnfermero(enfermeroId)
        )
    }

    /**
     * guardarCambios: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun guardarCambios(
        nombre: String,
        apellido: String,
        numLicencia: String?,
        turno: TurnoTrabajo
    ) {
        val estadoActual = _uiState.value
        if (estadoActual !is PerfilUiState.Listo) return

        viewModelScope.launch {
            val usuarioActualizado = estadoActual.usuario.copy(
                nombre = nombre.trim(),
                apellido = apellido.trim(),
                numLicencia = numLicencia?.trim()?.takeIf { it.isNotBlank() },
                turno = turno
            )
            usuarioDao.actualizarUsuario(usuarioActualizado)
            _uiState.value = PerfilUiState.Listo(usuarioActualizado)
            _guardadoExitoso.value = true
        }
    }

    /**
     * resetGuardadoExitoso: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun resetGuardadoExitoso() {
        _guardadoExitoso.value = false
    }

    /**
     * mensajeMostrado: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun mensajeMostrado() {
        _mensaje.value = null
    }

    /**
     * mostrarInfo: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun mostrarInfo(texto: String) {
        _mensaje.value = texto
    }

    /**
     * iniciarCierreSesion: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun iniciarCierreSesion(onListo: () -> Unit) {
        if (_cerrandoSesion.value) return
        _cerrandoSesion.value = true
        _mensaje.value = null
        onListo()
    }

    /**
     * Registro rápido e instantáneo de usuario local con persistencia completa en Room.
     * Asigna un `username` único y una contraseña por defecto ("clinick123") para habilitar
     * reingresos posteriores desde LoginScreen sin errores de 'Usuario no encontrado'.
     */
    /**
     * crearOCambiarCuentaRapida: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun crearOCambiarCuentaRapida(
        nombreCompleto: String,
        legajo: String,
        rol: String
    ) {
        if (nombreCompleto.isBlank()) {
            _mensaje.value = "El nombre completo es obligatorio."
            return
        }

        viewModelScope.launch {
            val partes = nombreCompleto.trim().split(" ", limit = 2)
            val nombre = partes.getOrElse(0) { nombreCompleto.trim() }
            val apellido = partes.getOrElse(1) { "" }

            // Usa el legajo/licencia como base si existe, o genera uno basado en el nombre
            val baseUsername = legajo.trim().lowercase().ifBlank {
                generarUsernameUnico(nombre, apellido)
            }

            val usernameFinal = resolverUsernameSinDuplicados(baseUsername)
            val salt = SecurityUtils.generateSalt()
            val passwordHash = SecurityUtils.hashPassword("clinick123", salt)

            val nuevoUsuario = UsuarioEntity(
                username = usernameFinal,
                passwordHash = passwordHash,
                salt = salt,
                nombre = nombre,
                apellido = apellido,
                rol = rol.trim().ifBlank { "ENFERMERO" },
                numLicencia = legajo.trim().takeIf { it.isNotBlank() },
                turno = TurnoTrabajo.MANANA
            )

            val nuevoId = usuarioDao.registrarUsuario(nuevoUsuario)

            // Sincronización inmediata de sesión en DataStore/SharedPreferences
            sessionManager.saveSession(
                userId = nuevoId,
                username = usernameFinal,
                role = nuevoUsuario.rol
            )

            _mensaje.value = "Cuenta asignada: usuario '$usernameFinal' (clave: clinick123)"
            cargarUsuarioActivo()
        }
    }

    /**
     * generarUsernameUnico: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private suspend fun generarUsernameUnico(nombre: String, apellido: String): String {
        val base = (nombre + apellido)
            .lowercase()
            .replace(Regex("[^a-z0-9]"), "")
            .ifBlank { "usuario" }

        return resolverUsernameSinDuplicados(base)
    }

    /**
     * resolverUsernameSinDuplicados: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private suspend fun resolverUsernameSinDuplicados(baseUsername: String): String {
        var candidato = baseUsername
        var sufijo = 1
        while (usuarioDao.getUsuarioByUsername(candidato) != null) {
            candidato = "$baseUsername$sufijo"
            sufijo++
        }
        return candidato
    }
}
