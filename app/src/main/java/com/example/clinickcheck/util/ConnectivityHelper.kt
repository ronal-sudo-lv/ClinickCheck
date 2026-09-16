package com.example.clinickcheck.util


/**
 * Archivo: com/example/clinickcheck/util/ConnectivityHelper.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utilidad para verificar si el dispositivo tiene conexion a internet en
 * este momento. Se usa para decidir si la busqueda de antecedentes medicos
 * se hace contra la API publica o si se debe mostrar el indicador de
 * "Modo Offline" y permitir el ingreso manual.
 */
@Singleton
    /**
     * ConnectivityHelper: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class ConnectivityHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * hayConexionInternet: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun hayConexionInternet(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
