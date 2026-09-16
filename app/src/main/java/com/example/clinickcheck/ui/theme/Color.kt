package com.example.clinickcheck.ui.theme


/**
 * Archivo: com/example/clinickcheck/ui/theme/Color.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.compose.ui.graphics.Color

// Paleta médica profesional (especificación del proyecto)
val MedicoPrimary = Color(0xFF0A4D68)          // Azul Médico Profundo
val MedicoOnPrimary = Color(0xFFFFFFFF)
val MedicoPrimaryContainer = Color(0xFFD3E6EC)
val MedicoOnPrimaryContainer = Color(0xFF00212E)

val MedicoSecondary = Color(0xFF088395)        // Turquesa Clínico
val MedicoOnSecondary = Color(0xFFFFFFFF)
val MedicoSecondaryContainer = Color(0xFFD6F0F3)
val MedicoOnSecondaryContainer = Color(0xFF002F35)

val MedicoTertiary = Color(0xFF1565C0)         // Azul para estado ALTA
val MedicoOnTertiary = Color(0xFFFFFFFF)
val MedicoTertiaryContainer = Color(0xFFD6E4F7)
val MedicoOnTertiaryContainer = Color(0xFF0B2E4D)

val MedicoBackground = Color(0xFFF4F7F9)       // Gris/Azul claro de fondo
val MedicoSurface = Color(0xFFFFFFFF)
val MedicoSurfaceVariant = Color(0xFFE1E8ED)

val MedicoErrorContainer = Color(0xFFFFD2D2)   // Estados CRITICO y Alertas
val MedicoOnErrorContainer = Color(0xFF8B0000)
val MedicoError = Color(0xFFB00020)

// Colores fijos para los badges de estado del paciente en la lista
val EstadoEstableColor = Color(0xFF2E7D32)          // Verde
val EstadoEstableContainer = Color(0xFFD7EFD9)
val EstadoCriticoColor = Color(0xFF8B0000)          // Rojo
val EstadoCriticoContainer = Color(0xFFFFD2D2)
val EstadoAltaColor = Color(0xFF0D47A1)             // Azul
val EstadoAltaContainer = Color(0xFFD6E4F7)

// Compatibilidad con nombres previos usados en algunas pantallas
val BluePrimary = MedicoPrimary
val BlueOnPrimary = MedicoOnPrimary
val BlueContainer = MedicoPrimaryContainer
val BlueOnContainer = MedicoOnPrimaryContainer
val BlueSecondary = MedicoSecondary
val BlueOnSecondary = MedicoOnSecondary
val BlueSecondaryContainer = MedicoSecondaryContainer
val BackgroundLight = MedicoBackground
val SurfaceLight = MedicoSurface
val SurfaceVariant = MedicoSurfaceVariant
val AlertRed = MedicoError
val AlertRedContainer = MedicoErrorContainer
val WarningAmber = Color(0xFFB9791E)
val WarningAmberContainer = Color(0xFFFBEDD6)
