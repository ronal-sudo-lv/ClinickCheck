package com.example.clinickcheck.ui.paciente


/**
 * Archivo: com/example/clinickcheck/ui/paciente/PacientesScreen.kt
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.clinickcheck.local.entity.EstadoPaciente
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.ui.theme.EstadoAltaColor
import com.example.clinickcheck.ui.theme.EstadoAltaContainer
import com.example.clinickcheck.ui.theme.EstadoCriticoColor
import com.example.clinickcheck.ui.theme.EstadoCriticoContainer
import com.example.clinickcheck.ui.theme.EstadoEstableColor
import com.example.clinickcheck.ui.theme.EstadoEstableContainer
import com.example.clinickcheck.util.formatearCama

@OptIn(ExperimentalMaterial3Api::class)
@Composable
    /**
     * PacientesScreen: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun PacientesScreen(
    onPacienteClick: (Long) -> Unit,
    onAgregarPacienteClick: () -> Unit,
    onPerfilClick: () -> Unit,
    viewModel: PacienteViewModel = hiltViewModel()
) {
    val pacientes by viewModel.pacientes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val estadoFiltro by viewModel.estadoFiltro.collectAsState()
    val totalPacientes by viewModel.totalPacientes.collectAsState()
    val pacientesCriticos by viewModel.pacientesCriticosCount.collectAsState()
    val alertasPendientes by viewModel.alertasPendientesCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ClinickCheck", style = MaterialTheme.typography.titleMedium)
                        Text("Gestión de Pacientes", style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    IconButton(onClick = onPerfilClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Mi Perfil",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregarPacienteClick,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Paciente")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // --- Dashboard: 3 tarjetas de métricas en tiempo real ---
            DashboardMetricas(
                totalPacientes = totalPacientes,
                pacientesCriticos = pacientesCriticos,
                alertasPendientes = alertasPendientes
            )

            // Campo de Búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar por nombre, apellido o DNI...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Filtros por estado clínico
            FiltroEstadoRow(
                estadoSeleccionado = estadoFiltro,
                onEstadoSeleccionado = { viewModel.onEstadoFiltroChanged(it) }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Lista de Pacientes
            if (pacientes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "No hay pacientes registrados." else "No se encontraron resultados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pacientes, key = { it.paciente.idPaciente }) { item ->
                        PacienteCard(
                            paciente = item.paciente,
                            tieneAlertaActiva = item.tieneAlertaActiva,
                            onClick = { onPacienteClick(item.paciente.idPaciente) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
    /**
     * DashboardMetricas: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun DashboardMetricas(
    totalPacientes: Int,
    pacientesCriticos: Int,
    alertasPendientes: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TarjetaMetrica(
            titulo = "Total Pacientes",
            valor = totalPacientes.toString(),
            colorFondo = MaterialTheme.colorScheme.secondaryContainer,
            colorTexto = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.weight(1f)
        )
        TarjetaMetrica(
            titulo = "En Estado Crítico",
            valor = pacientesCriticos.toString(),
            colorFondo = EstadoCriticoContainer,
            colorTexto = EstadoCriticoColor,
            modifier = Modifier.weight(1f)
        )
        TarjetaMetrica(
            titulo = "Alertas Pendientes",
            valor = alertasPendientes.toString(),
            colorFondo = EstadoCriticoContainer,
            colorTexto = EstadoCriticoColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
    /**
     * TarjetaMetrica: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun TarjetaMetrica(
    titulo: String,
    valor: String,
    colorFondo: androidx.compose.ui.graphics.Color,
    colorTexto: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineSmall,
                color = colorTexto
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = colorTexto,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
    /**
     * FiltroEstadoRow: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun FiltroEstadoRow(
    estadoSeleccionado: EstadoPaciente?,
    onEstadoSeleccionado: (EstadoPaciente?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = estadoSeleccionado == null,
                onClick = { onEstadoSeleccionado(null) },
                label = { Text("Todos") }
            )
        }
        items(EstadoPaciente.entries.toList()) { estado ->
            FilterChip(
                selected = estadoSeleccionado == estado,
                onClick = {
                    onEstadoSeleccionado(if (estadoSeleccionado == estado) null else estado)
                },
                label = { Text(estado.name) }
            )
        }
    }
}

@Composable
    /**
     * PacienteCard: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun PacienteCard(
    paciente: PacienteEntity,
    tieneAlertaActiva: Boolean,
    onClick: () -> Unit
) {
    val (colorContenedor, colorTexto) = when (paciente.estado.uppercase()) {
        EstadoPaciente.CRITICO.name -> EstadoCriticoContainer to EstadoCriticoColor
        EstadoPaciente.ALTA.name -> EstadoAltaContainer to EstadoAltaColor
        else -> EstadoEstableContainer to EstadoEstableColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${paciente.apellido}, ${paciente.nombre}",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                    if (tieneAlertaActiva) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(EstadoCriticoContainer)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Alerta activa",
                                tint = EstadoCriticoColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorContenedor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = paciente.estado,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorTexto
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "DNI: ${paciente.dni}", style = MaterialTheme.typography.bodyMedium)
                Text(text = formatearCama(paciente.cama ?: ""), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
