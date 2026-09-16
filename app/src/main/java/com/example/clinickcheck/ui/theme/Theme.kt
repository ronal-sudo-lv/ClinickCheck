package com.example.clinickcheck.ui.theme


/**
 * Archivo: com/example/clinickcheck/ui/theme/Theme.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = MedicoPrimary,
    onPrimary = MedicoOnPrimary,
    primaryContainer = MedicoPrimaryContainer,
    onPrimaryContainer = MedicoOnPrimaryContainer,
    secondary = MedicoSecondary,
    onSecondary = MedicoOnSecondary,
    secondaryContainer = MedicoSecondaryContainer,
    onSecondaryContainer = MedicoOnSecondaryContainer,
    tertiary = MedicoTertiary,
    onTertiary = MedicoOnTertiary,
    tertiaryContainer = MedicoTertiaryContainer,
    onTertiaryContainer = MedicoOnTertiaryContainer,
    background = MedicoBackground,
    surface = MedicoSurface,
    surfaceVariant = MedicoSurfaceVariant,
    error = MedicoError,
    errorContainer = MedicoErrorContainer,
    onErrorContainer = MedicoOnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = MedicoSecondary,
    secondary = MedicoPrimary,
    tertiary = MedicoTertiary,
    error = MedicoError,
    errorContainer = MedicoErrorContainer,
    onErrorContainer = MedicoOnErrorContainer
)

@Composable
    /**
     * ClinickCheckTheme: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun ClinickCheckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
