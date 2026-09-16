



package com.example.clinickcheck.ui.inicio


/**
 * Archivo: com/example/clinickcheck/ui/inicio/InicioScreen.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.ui.paciente.PacienteCard
import com.example.clinickcheck.ui.theme.EstadoCriticoColor
import com.example.clinickcheck.ui.theme.EstadoCriticoContainer
import com.example.clinickcheck.ui.theme.WarningAmber
import com.example.clinickcheck.ui.theme.WarningAmberContainer
import com.example.clinickcheck.util.formatearCama

/**
 * Pantalla de Inicio (Home/Dashboard). Es la puerta de entrada del turno del
 * enfermero: saludo, accesos rapidos, resumen de estado y lista de
 * pacientes. Los accesos rapidos a Signos Vitales y Aplicar Dosis abren un
 * selector de paciente y luego navegan exclusivamente a su propio modulo
 * (SignosVitalesScreen o MedicamentosScreen).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
    /**
     * InicioScreen: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun InicioScreen(
    onRegistrarVitalesClick: (pacienteId: Long) -> Unit,
    onAplicarDosisClick: (pacienteId: Long) -> Unit,
    onNuevoPacienteClick: () -> Unit,
    onPacienteClick: (pacienteId: Long) -> Unit,
    onVerTodosLosPacientesClick: () -> Unit,
    onNavigateToPacientesCriticos: () -> Unit = {},
    onNavigateToDosisPendientes: () -> Unit = {},
    viewModel: InicioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val misPacientes by viewModel.misPacientes.collectAsState()
    val pacientesCriticos by viewModel.pacientesCriticosCount.collectAsState()
    val dosisPendientes by viewModel.dosisPendientes.collectAsState()

    var accionPendiente by remember { mutableStateOf<AccionRapida?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ClinickCheck") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CabeceraBienvenida(nombreUsuario = uiState.nombreUsuario, turnoTexto = uiState.turnoTexto)
            }

            item {
                AccesosRapidos(
                    onRegistrarVitalesClick = { accionPendiente = AccionRapida.REGISTRAR_VITALES },
                    onAplicarDosisClick = { accionPendiente = AccionRapida.APLICAR_DOSIS },
                    onNuevoPacienteClick = onNuevoPacienteClick
                )
            }

            item {
                TarjetaEstadoDelTurno(
                    pacientesCriticos = pacientesCriticos,
                    dosisPendientes = dosisPendientes,
                    onNavigateToPacientesCriticos = onNavigateToPacientesCriticos,
                    onNavigateToDosisPendientes = onNavigateToDosisPendientes
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis Pacientes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onVerTodosLosPacientesClick) {
                        Text("Ver todos")
                    }
                }
            }

            if (misPacientes.isEmpty()) {
                item {
                    Text(
                        text = "No hay pacientes asignados en este turno.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(misPacientes, key = { it.paciente.idPaciente }) { item ->
                    PacienteCard(
                        paciente = item.paciente,
                        tieneAlertaActiva = item.tieneAlertaActiva,
                        onClick = { onPacienteClick(item.paciente.idPaciente) }
                    )
                }
            }
        }
    }

    accionPendiente?.let { accion ->
        SeleccionarPacienteDialog(
            pacientes = misPacientes.map { it.paciente },
            titulo = if (accion == AccionRapida.REGISTRAR_VITALES) {
                "Seleccionar paciente — Signos Vitales"
            } else {
                "Seleccionar paciente — Aplicar Dosis"
            },
            onSeleccionar = { pacienteId ->
                when (accion) {
                    AccionRapida.REGISTRAR_VITALES -> onRegistrarVitalesClick(pacienteId)
                    AccionRapida.APLICAR_DOSIS -> onAplicarDosisClick(pacienteId)
                }
                accionPendiente = null
            },
            onDismiss = { accionPendiente = null }
        )
    }
}

    /**
     * AccionRapida: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
private enum class AccionRapida { REGISTRAR_VITALES, APLICAR_DOSIS }

@Composable
    /**
     * CabeceraBienvenida: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun CabeceraBienvenida(nombreUsuario: String, turnoTexto: String) {
    Column {
        Text(
            text = "Hola, $nombreUsuario",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        AssistChip(onClick = {}, label = { Text(turnoTexto) })
    }
}

@Composable
    /**
     * AccesosRapidos: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun AccesosRapidos(
    onRegistrarVitalesClick: () -> Unit,
    onAplicarDosisClick: () -> Unit,
    onNuevoPacienteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AccesoRapidoCard(
            icono = Icons.Default.Favorite,
            texto = "Registrar Vitales",
            modifier = Modifier.weight(1f),
            onClick = onRegistrarVitalesClick
        )
        AccesoRapidoCard(
            icono = Icons.Default.Medication,
            texto = "Aplicar Dosis",
            modifier = Modifier.weight(1f),
            onClick = onAplicarDosisClick
        )
        AccesoRapidoCard(
            icono = Icons.Default.PersonAdd,
            texto = "Nuevo Paciente",
            modifier = Modifier.weight(1f),
            onClick = onNuevoPacienteClick
        )
    }
}

@Composable
    /**
     * AccesoRapidoCard: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun AccesoRapidoCard(
    icono: ImageVector,
    texto: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = texto,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
    /**
     * TarjetaEstadoDelTurno: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun TarjetaEstadoDelTurno(
    pacientesCriticos: Int,
    dosisPendientes: Int,
    onNavigateToPacientesCriticos: () -> Unit,
    onNavigateToDosisPendientes: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Estado del Turno",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IndicadorEstado(
                    valor = pacientesCriticos,
                    etiqueta = "Pacientes en Estado Crítico",
                    activo = pacientesCriticos > 0,
                    colorFondo = EstadoCriticoContainer,
                    colorTexto = EstadoCriticoColor,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToPacientesCriticos() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                IndicadorEstado(
                    valor = dosisPendientes,
                    etiqueta = "Dosis Pendientes del Turno",
                    activo = dosisPendientes > 0,
                    colorFondo = WarningAmberContainer,
                    colorTexto = WarningAmber,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToDosisPendientes() }
                )
            }
        }
    }
}

@Composable
    /**
     * IndicadorEstado: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun IndicadorEstado(
    valor: Int,
    etiqueta: String,
    activo: Boolean,
    colorFondo: androidx.compose.ui.graphics.Color,
    colorTexto: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val fondoFinal = if (activo) colorFondo else MaterialTheme.colorScheme.surfaceVariant
    val textoFinal = if (activo) colorTexto else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(fondoFinal)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (activo) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = textoFinal,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Column {
            Text(
                text = valor.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = textoFinal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = textoFinal
            )
        }
    }
}

@Composable
    /**
     * SeleccionarPacienteDialog: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun SeleccionarPacienteDialog(
    pacientes: List<PacienteEntity>,
    titulo: String,
    onSeleccionar: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titulo) },
        text = {
            if (pacientes.isEmpty()) {
                Text("No hay pacientes activos para seleccionar.")
            } else {
                Column {
                    pacientes.forEach { paciente ->
                        Text(
                            text = "${paciente.apellido}, ${paciente.nombre} — ${formatearCama(paciente.cama ?: "")}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSeleccionar(paciente.idPaciente) }
                                .padding(vertical = 12.dp)
                        )
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
