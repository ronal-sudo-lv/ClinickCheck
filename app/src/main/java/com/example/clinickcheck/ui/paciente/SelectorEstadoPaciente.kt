package com.example.clinickcheck.ui.paciente


/**
 * Archivo: com/example/clinickcheck/ui/paciente/SelectorEstadoPaciente.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Componente visual reutilizable para cambiar el estado clínico del paciente.
 * Permite seleccionar entre los estados relevantes para el turno del paciente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
    /**
     * SelectorEstadoPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun SelectorEstadoPaciente(
    estadoActual: String,
    onEstadoCambiado: (nuevoEstado: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listaEstados = listOf(
        "ESTABLE" to "ESTABLE",
        "CRITICO" to "CRÍTICO",
        "OBSERVACION" to "OBSERVACIÓN",
        "EN_ALTA" to "EN ALTA",
        "ALTA" to "ALTA"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Estado Clínico del Paciente",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listaEstados.forEach { (valor, etiqueta) ->
                    val esSeleccionado = estadoActual.equals(valor, ignoreCase = true)

                    FilterChip(
                        selected = esSeleccionado,
                        onClick = { onEstadoCambiado(valor) },
                        label = {
                            Text(
                                text = etiqueta,
                                fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (valor) {
                                "CRITICO" -> MaterialTheme.colorScheme.errorContainer
                                "EN_ALTA", "ALTA" -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.primaryContainer
                            },
                            selectedLabelColor = when (valor) {
                                "CRITICO" -> MaterialTheme.colorScheme.onErrorContainer
                                "EN_ALTA", "ALTA" -> MaterialTheme.colorScheme.onTertiaryContainer
                                else -> MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        )
                    )
                }
            }
        }
    }
}
