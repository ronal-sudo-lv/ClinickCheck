package com.example.clinickcheck.ui.inicio


/**
 * Archivo: com/example/clinickcheck/ui/inicio/SelectorPacienteScreen.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.util.formatearCama

@OptIn(ExperimentalMaterial3Api::class)
@Composable
    /**
     * SelectorPacienteScreen: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun SelectorPacienteScreen(
    titulo: String,
    pacientes: List<PacienteEntity>,
    onSeleccionar: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(titulo) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(pacientes, key = { it.idPaciente }) { paciente ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSeleccionar(paciente.idPaciente) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "${paciente.apellido}, ${paciente.nombre}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = formatearCama(paciente.cama ?: ""),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider()
            }
        }
    }
}
