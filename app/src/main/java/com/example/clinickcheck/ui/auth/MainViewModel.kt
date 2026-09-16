package com.example.clinickcheck.ui.auth


/**
 * Archivo: com/example/clinickcheck/ui/auth/MainViewModel.kt
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
import com.example.clinickcheck.local.preferences.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
    /**
     * MainViewModel: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class MainViewModel @Inject constructor(
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    init {
        // DESCOMENTAR SOLO PARA PRUEBAS: Limpia la sesión guardada al arrancar la app
        // viewModelScope.launch { userSessionManager.clearSession() }
    }

    val isLoggedIn: StateFlow<Boolean?> = userSessionManager.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
