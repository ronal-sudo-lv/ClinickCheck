package com.example.clinickcheck.ui.paciente


/**
 * Archivo: com/example/clinickcheck/ui/paciente/MedicamentosScreen.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
    /**
     * MedicamentosScreen: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun MedicamentosScreen(
    onBackClick: () -> Unit,
    viewModel: MedicamentosViewModel = hiltViewModel()
) {
    val medicamentos by viewModel.medicamentos.collectAsState()
    val progreso by viewModel.progreso.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoAgregar by remember { mutableStateOf(false) }
    // Confirmacion de dosis: se pide doble confirmacion antes de marcar una
    // toma como administrada, para evitar registros accidentales.
    var medicamentoParaConfirmar by remember { mutableStateOf<Medicamento?>(null) }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    val administradas = medicamentos.count { it.administrado }
    val total = medicamentos.size

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Control de Medicamentos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogoAgregar = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar medicamento")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // --- Barra de progreso del turno ---
            Text(
                text = "Progreso de turno: $administradas de $total dosis administradas",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                medicamentos.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay medicamentos programados.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(medicamentos, key = { it.idDetalle }) { medicamento ->
                            TarjetaMedicamento(
                                medicamento = medicamento,
                                onCheckedChange = { medicamentoParaConfirmar = medicamento }
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoAgregar) {
        val queryMedicamento by viewModel.queryMedicamento.collectAsState()
        val sugerenciasMedicamentos by viewModel.sugerenciasMedicamentos.collectAsState()
        val cargandoSugerencias by viewModel.cargandoSugerencias.collectAsState()

        AgregarMedicamentoDialog(
            onDismiss = {
                viewModel.limpiarSugerenciasMedicamentos()
                mostrarDialogoAgregar = false
            },
            onConfirmar = { nombre, dosis, via, hora ->
                viewModel.agregarMedicamento(nombre, dosis, via, hora)
                viewModel.limpiarSugerenciasMedicamentos()
                mostrarDialogoAgregar = false
            },
            queryMedicamento = queryMedicamento,
            sugerenciasMedicamentos = sugerenciasMedicamentos,
            cargandoSugerencias = cargandoSugerencias,
            onQueryMedicamentoChange = { viewModel.onQueryMedicamentoChanged(it) },
            onSelectMedicamento = { nombre ->
                viewModel.seleccionarSugerenciaMedicamento(nombre)
            }
        )
    }

    medicamentoParaConfirmar?.let { medicamento ->
        AlertDialog(
            onDismissRequest = { medicamentoParaConfirmar = null },
            title = { Text("Confirmar administración") },
            text = {
                Text(
                    "¿Confirma que administró ${medicamento.nombre} ${medicamento.dosis} " +
                        "vía ${medicamento.via} (${medicamento.horaProgramada}) a este paciente? " +
                        "Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.marcarComoAdministrado(medicamento)
                    medicamentoParaConfirmar = null
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { medicamentoParaConfirmar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
    /**
     * TarjetaMedicamento: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun TarjetaMedicamento(
    medicamento: Medicamento,
    onCheckedChange: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (medicamento.administrado) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = medicamento.administrado,
                onCheckedChange = { onCheckedChange() },
                enabled = !medicamento.administrado
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${medicamento.horaProgramada} — ${medicamento.nombre} ${medicamento.dosis} ${medicamento.via}",
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (medicamento.administrado) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (medicamento.administrado) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = if (medicamento.administrado) {
                        "Administrado · ${medicamento.enfermero ?: "No identificado"}"
                    } else {
                        "Pendiente"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (medicamento.administrado) FontWeight.Normal else FontWeight.Medium,
                    color = if (medicamento.administrado) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}

/**
 * Dialogo para agregar un nuevo medicamento a la agenda del paciente. Usa
 * estados locales (remember { mutableStateOf("") }) porque los campos solo
 * viven mientras el dialogo esta abierto.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
    /**
     * AgregarMedicamentoDialog: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun AgregarMedicamentoDialog(
    onDismiss: () -> Unit,
    onConfirmar: (nombre: String, dosis: String, via: String, hora: String) -> Unit,
    queryMedicamento: String,
    sugerenciasMedicamentos: List<String>,
    cargandoSugerencias: Boolean,
    onQueryMedicamentoChange: (String) -> Unit,
    onSelectMedicamento: (String) -> Unit
) {
    var nombre by remember { mutableStateOf(queryMedicamento) }
    var dosis by remember { mutableStateOf("") }
    var via by remember { mutableStateOf("Oral") }
    var hora by remember { mutableStateOf("") }
    var viaMenuExpandido by remember { mutableStateOf(false) }
    var nombreMenuExpandido by remember { mutableStateOf(false) }
    val opcionesVia = listOf("Oral", "IV", "IM", "SC", "Tópica")

    LaunchedEffect(queryMedicamento) {
        nombre = queryMedicamento
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Medicamento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = nombreMenuExpandido && sugerenciasMedicamentos.isNotEmpty(),
                    onExpandedChange = { expanded ->
                        if (sugerenciasMedicamentos.isNotEmpty()) {
                            nombreMenuExpandido = expanded
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            onQueryMedicamentoChange(it)
                            nombreMenuExpandido = it.length >= 3 && sugerenciasMedicamentos.isNotEmpty()
                        },
                        label = { Text("Nombre del medicamento") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = {
                            if (cargandoSugerencias) {
                                CircularProgressIndicator(modifier = Modifier.height(18.dp), strokeWidth = 2.dp)
                            } else {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = nombreMenuExpandido && sugerenciasMedicamentos.isNotEmpty())
                            }
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = nombreMenuExpandido && sugerenciasMedicamentos.isNotEmpty(),
                        onDismissRequest = { nombreMenuExpandido = false }
                    ) {
                        sugerenciasMedicamentos.forEach { sugerencia ->
                            DropdownMenuItem(
                                text = { Text(sugerencia) },
                                onClick = {
                                    nombre = sugerencia
                                    onSelectMedicamento(sugerencia)
                                    nombreMenuExpandido = false
                                }
                            )
                        }
                    }
                }
                if (cargandoSugerencias && sugerenciasMedicamentos.isEmpty()) {
                    Text("Buscando medicamentos...", style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = dosis,
                    onValueChange = { dosis = it },
                    label = { Text("Dosis (ej. 500mg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(
                    expanded = viaMenuExpandido,
                    onExpandedChange = { viaMenuExpandido = it }
                ) {
                    OutlinedTextField(
                        value = via,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vía de administración") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viaMenuExpandido) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = viaMenuExpandido,
                        onDismissRequest = { viaMenuExpandido = false }
                    ) {
                        opcionesVia.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    via = opcion
                                    viaMenuExpandido = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = hora,
                    onValueChange = { hora = it },
                    label = { Text("Hora programada (ej. 08:00)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmar(nombre, dosis, via, hora) },
                enabled = nombre.isNotBlank() && dosis.isNotBlank()
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
