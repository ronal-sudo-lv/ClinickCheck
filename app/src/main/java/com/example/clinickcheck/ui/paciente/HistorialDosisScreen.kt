package com.example.clinickcheck.ui.paciente


/**
 * Archivo: com/example/clinickcheck/ui/paciente/HistorialDosisScreen.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.clinickcheck.local.entity.DosisRegistroEntity
import com.example.clinickcheck.local.entity.TurnoTrabajo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
    /**
     * HistorialDosisScreen: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun HistorialDosisScreen(
    onBackClick: () -> Unit,
    viewModel: PacienteDetalleViewModel = hiltViewModel()
) {
    val historialDosisCompleto by viewModel.historialDosis.collectAsState()
    val soloMisRegistros by viewModel.soloMisRegistrosDosis.collectAsState()
    val enfermerosInfo by viewModel.enfermerosInfo.collectAsState()
    var turnoFiltro by remember { mutableStateOf<TurnoTrabajo?>(null) }

    // Inmutabilidad/auditoria: el registro seleccionado para anular (si hay
    // alguno) mientras se pide la razon obligatoria.
    var registroParaAnular by remember { mutableStateOf<DosisRegistroEntity?>(null) }

    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()) }

    // Filtro adicional por turno del enfermero que administro la dosis
    // (el filtro "Mis Registros" / "Todos" ya se aplica en el ViewModel).
    val historialDosis = remember(historialDosisCompleto, turnoFiltro, enfermerosInfo) {
        if (turnoFiltro == null) {
            historialDosisCompleto
        } else {
            historialDosisCompleto.filter { registro ->
                val info = registro.enfermeroId?.let { enfermerosInfo[it] }
                info?.turno == turnoFiltro
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Dosis Administradas") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Filtro de trazabilidad: Mis Registros vs Todos los Registros
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (soloMisRegistros) "Mostrando: Mis Registros" else "Mostrando: Todos los Registros",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = soloMisRegistros,
                    onCheckedChange = { viewModel.toggleSoloMisRegistrosDosis() }
                )
            }

            // Filtro por turno del enfermero
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = turnoFiltro == null,
                        onClick = { turnoFiltro = null },
                        label = { Text("Todos los turnos") }
                    )
                }
                items(TurnoTrabajo.entries.toList()) { turno ->
                    FilterChip(
                        selected = turnoFiltro == turno,
                        onClick = { turnoFiltro = if (turnoFiltro == turno) null else turno },
                        label = { Text(turno.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (historialDosis.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay registro de dosis administradas con estos filtros.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historialDosis, key = { it.idDosis }) { registro ->
                        ItemHistorialDosis(
                            registro = registro,
                            nombreEnfermero = registro.enfermeroId?.let { enfermerosInfo[it]?.nombreCompleto },
                            sdf = sdf,
                            onAnularClick = { registroParaAnular = registro }
                        )
                    }
                }
            }
        }
    }

    registroParaAnular?.let { registro ->
        AnularRegistroDialog(
            titulo = "Anular dosis administrada",
            onConfirmar = { razon ->
                viewModel.anularDosis(registro.idDosis, razon)
                registroParaAnular = null
            },
            onDismiss = { registroParaAnular = null }
        )
    }
}

@Composable
    /**
     * ItemHistorialDosis: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun ItemHistorialDosis(
    registro: DosisRegistroEntity,
    nombreEnfermero: String?,
    sdf: SimpleDateFormat,
    onAnularClick: () -> Unit
) {
    val colorTexto = if (registro.esAnulado) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val decoracionTexto = if (registro.esAnulado) TextDecoration.LineThrough else TextDecoration.None

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Fecha: ${sdf.format(Date(registro.timestamp))}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (registro.esAnulado) colorTexto else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Dosis Aplicada: ${registro.dosisRealizada ?: "S/D"}", color = colorTexto, textDecoration = decoracionTexto)
                Text(text = "Estado: ${registro.estado}", color = colorTexto, textDecoration = decoracionTexto)
                Text(text = "Enfermero: ${nombreEnfermero ?: "No identificado"}", color = colorTexto, textDecoration = decoracionTexto)
                registro.observaciones?.let {
                    Text(text = "Observaciones: $it", style = MaterialTheme.typography.bodySmall, color = colorTexto, textDecoration = decoracionTexto)
                }
                if (registro.esAnulado) {
                    Text(
                        text = "Anulado: ${registro.razonAnulacion ?: "Sin razón registrada"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            if (!registro.esAnulado) {
                IconButton(onClick = onAnularClick) {
                    Icon(Icons.Default.Block, contentDescription = "Anular dosis")
                }
            }
        }
    }
}
