package com.example.clinickcheck.ui.paciente


/**
 * Archivo: com/example/clinickcheck/ui/paciente/PacienteDetalleScreen.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.clinickcheck.local.entity.AlertaEntity
import com.example.clinickcheck.local.entity.AntecedenteEntity
import com.example.clinickcheck.local.entity.SignoVitalEntity
import com.example.clinickcheck.local.relation.PrescripcionConDetalles
import com.example.clinickcheck.data.repository.AntecedenteSugerido
import com.example.clinickcheck.local.relation.DetalleConFarmaco
import com.example.clinickcheck.ui.auth.AuthViewModel
import com.example.clinickcheck.util.formatearCama
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
    /**
     * PacienteDetalleScreen: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun PacienteDetalleScreen(
    onBackClick: () -> Unit,
    onVerHistorialDosisClick: (Long) -> Unit,
    viewModel: PacienteDetalleViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    antecedenteViewModel: AntecedenteViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val paciente by viewModel.paciente.collectAsState()
    val historialSignos by viewModel.historialSignos.collectAsState()
    val prescripciones by viewModel.prescripciones.collectAsState()
    val alertas by viewModel.alertas.collectAsState()
    val antecedentes by antecedenteViewModel.antecedentes.collectAsState()

    var pa by remember { mutableStateOf("") }
    var fc by remember { mutableStateOf("") }
    var temp by remember { mutableStateOf("") }
    var spo2 by remember { mutableStateOf("") }
    var eva by remember { mutableStateOf("") }

    // Inmutabilidad/auditoria: ningun historial se borra. Estas variables
    // guardan que registro concreto se esta anulando mientras el dialogo
    // de razon obligatoria esta abierto.
    var signoParaAnular by remember { mutableStateOf<SignoVitalEntity?>(null) }
    var antecedenteParaAnular by remember { mutableStateOf<AntecedenteEntity?>(null) }

    // --- REGLAS DE VALIDACIÓN DE RANGOS ---
    val paValida = remember(pa) {
        pa.isEmpty() || pa.matches(Regex("""^\d{2,3}/\d{2,3}$"""))
    }
    val fcValida = remember(fc) {
        fc.isEmpty() || (fc.toIntOrNull() in 30..220)
    }
    val tempValida = remember(temp) {
        temp.isEmpty() || (temp.toDoubleOrNull()?.let { it in 30.0..45.0 } == true)
    }
    val spo2Valida = remember(spo2) {
        spo2.isEmpty() || (spo2.toIntOrNull() in 0..100)
    }
    val evaValida = remember(eva) {
        eva.isEmpty() || (eva.toIntOrNull() in 0..10)
    }

    val alMenosUnCampoLleno = pa.isNotBlank() || fc.isNotBlank() || temp.isNotBlank() || spo2.isNotBlank() || eva.isNotBlank()
    val formularioValido = alMenosUnCampoLleno && paValida && fcValida && tempValida && spo2Valida && evaValida

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(paciente?.let { "${it.apellido}, ${it.nombre}" } ?: "Detalle Paciente") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.exportarPdf(context, antecedentes) { mensaje ->
                            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Exportar PDF")
                    }
                    IconButton(onClick = {
                        authViewModel.logout()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            paciente?.let { p ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Información General", style = MaterialTheme.typography.titleMedium)
                        Text(text = "DNI: ${p.dni}")
                        Text(text = "Ubicación: ${formatearCama(p.cama ?: "")}")
                        Text(text = "Estado: ${p.estado}")
                        SelectorEstadoPaciente(
                            estadoActual = p.estado,
                            onEstadoCambiado = { nuevoEstado ->
                                viewModel.actualizarEstadoPaciente(nuevoEstado)
                            }
                        )
                    }
                }
 
                // Sección de Alertas Activas
                if (alertas.any { !it.atendida }) {
                    Text(
                        text = "Alertas Pendientes",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        alertas.filter { !it.atendida }.forEach { alerta ->
                            ItemAlerta(alerta = alerta, onAtenderClick = { viewModel.atenderAlerta(alerta.idAlerta) })
                        }
                    }
                }

                Text(text = "Registro de Signos Vitales", style = MaterialTheme.typography.titleLarge)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = pa,
                                onValueChange = { pa = it },
                                label = { Text("PA (120/80)") },
                                leadingIcon = { Icon(Icons.Default.MonitorHeart, contentDescription = null) },
                                singleLine = true,
                                isError = !paValida,
                                supportingText = { if (!paValida) Text("Ej. 120/80") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = fc,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() }) fc = input
                                },
                                label = { Text("FC (lpm)") },
                                leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                                singleLine = true,
                                isError = !fcValida,
                                supportingText = { if (!fcValida) Text("30-220 bpm") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = temp,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() || it == '.' }) temp = input
                                },
                                label = { Text("Temp (°C)") },
                                leadingIcon = { Icon(Icons.Default.Thermostat, contentDescription = null) },
                                singleLine = true,
                                isError = !tempValida,
                                supportingText = { if (!tempValida) Text("30.0-45.0 °C") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = spo2,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() }) spo2 = input
                                },
                                label = { Text("SpO2 (%)") },
                                leadingIcon = { Icon(Icons.Default.Air, contentDescription = null) },
                                singleLine = true,
                                isError = !spo2Valida,
                                supportingText = { if (!spo2Valida) Text("0-100%") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        OutlinedTextField(
                            value = eva,
                            onValueChange = { input ->
                                if (input.all { it.isDigit() }) eva = input
                            },
                            label = { Text("Dolor EVA (0-10)") },
                            leadingIcon = { Icon(Icons.Default.Sick, contentDescription = null) },
                            singleLine = true,
                            isError = !evaValida,
                            supportingText = { if (!evaValida) Text("Escala 0 a 10") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (formularioValido) {
                                    viewModel.guardarSignosVitales(pa, fc, temp, spo2, eva)
                                    pa = ""
                                    fc = ""
                                    temp = ""
                                    spo2 = ""
                                    eva = ""
                                    Toast.makeText(context, "Signos vitales registrados correctamente", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = formularioValido,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar Signos Vitales")
                        }
                    }
                }

                if (historialSignos.isNotEmpty()) {
                    Text(text = "Historial Reciente", style = MaterialTheme.typography.titleLarge)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        historialSignos.forEach { signo ->
                            ItemSignoVital(
                                signo = signo,
                                onAnularClick = { signoParaAnular = signo }
                            )
                        }
                    }
                }

                SeccionAntecedentes(
                    antecedentes = antecedentes,
                    viewModel = antecedenteViewModel,
                    onAnularClick = { antecedenteParaAnular = it }
                )

                SeccionPrescripciones(
                    prescripciones = prescripciones,
                    onAplicarDosisClick = { farmacoId, idDetalle, dosis, via ->
                        viewModel.registrarAdministracionDosis(
                            farmacoId = farmacoId,
                            idDetalle = idDetalle,
                            dosis = dosis,
                            via = via,
                            observaciones = null
                        )
                    }
                )

                Button(
                    onClick = { paciente?.let { onVerHistorialDosisClick(it.idPaciente.toLong()) } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Ver Historial de Dosis Administradas")
                }

                Button(
                    onClick = {
                        viewModel.exportarPdf(context, antecedentes) { mensaje ->
                            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("Exportar Historia Clínica a PDF")
                }
            }
        }
    }

    signoParaAnular?.let { signo ->
        AnularRegistroDialog(
            titulo = "Anular registro de signos vitales",
            onConfirmar = { razon ->
                viewModel.anularSignoVital(signo.idSignoVital, razon)
                signoParaAnular = null
            },
            onDismiss = { signoParaAnular = null }
        )
    }

    antecedenteParaAnular?.let { antecedente ->
        AnularRegistroDialog(
            titulo = "Anular antecedente médico",
            onConfirmar = { razon ->
                antecedenteViewModel.anularAntecedente(antecedente, razon)
                antecedenteParaAnular = null
            },
            onDismiss = { antecedenteParaAnular = null }
        )
    }
}

@Composable
    /**
     * AnularRegistroDialog: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun AnularRegistroDialog(
    titulo: String,
    onConfirmar: (razon: String) -> Unit,
    onDismiss: () -> Unit
) {
    var razon by remember { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titulo) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Este registro no se elimina: quedará visible y marcado como anulado. Indique el motivo de la anulación (obligatorio).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = razon,
                    onValueChange = { razon = it },
                    label = { Text("Motivo de anulación") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmar(razon.trim()) },
                enabled = razon.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Anular")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
    /**
     * SeccionAntecedentes: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun SeccionAntecedentes(
    antecedentes: List<AntecedenteEntity>,
    viewModel: AntecedenteViewModel,
    onAnularClick: (AntecedenteEntity) -> Unit
) {
    val terminoBusqueda by viewModel.terminoBusqueda.collectAsState()
    val sugerencias by viewModel.sugerencias.collectAsState()
    val buscando by viewModel.buscando.collectAsState()
    val modoOffline by viewModel.modoOffline.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()

    var mostrarFormularioManual by remember { mutableStateOf(false) }
    var nombreManual by remember { mutableStateOf("") }
    var codigoManual by remember { mutableStateOf("") }
    var descripcionManual by remember { mutableStateOf("") }
    var esRelevanteManual by remember { mutableStateOf(false) }

    Text(text = "Antecedentes Médicos", style = MaterialTheme.typography.titleLarge)

    // Lista de antecedentes ya guardados para este paciente
    if (antecedentes.isEmpty()) {
        Text("No hay antecedentes médicos registrados.", style = MaterialTheme.typography.bodyMedium)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            antecedentes.forEach { antecedente ->
                ItemAntecedente(
                    antecedente = antecedente,
                    onAnularClick = { onAnularClick(antecedente) }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Indicador de conexión
    if (modoOffline) {
        AssistChip(
            onClick = { viewModel.refrescarConexion() },
            label = { Text("Modo Offline: ingreso manual disponible") },
            leadingIcon = { Icon(Icons.Default.WifiOff, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(8.dp))
    }

    // Buscador contra la API de NIH Clinical Tables
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = terminoBusqueda,
                    onValueChange = { viewModel.onTerminoBusquedaChanged(it) },
                    label = { Text("Buscar enfermedad / diagnóstico") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { viewModel.buscarEnApi() },
                    enabled = !buscando && terminoBusqueda.isNotBlank()
                ) {
                    if (buscando) {
                        CircularProgressIndicator(modifier = Modifier.height(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Buscar")
                    }
                }
            }

            mensajeError?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }

            if (sugerencias.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    sugerencias.forEach { sugerido ->
                        ItemSugerenciaAntecedente(
                            sugerido = sugerido,
                            onAgregarClick = { viewModel.agregarDesdeApi(sugerido) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clickable { mostrarFormularioManual = !mostrarFormularioManual },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (mostrarFormularioManual) "Ocultar ingreso manual" else "Agregar antecedente manualmente",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (mostrarFormularioManual || modoOffline) {
                OutlinedTextField(
                    value = nombreManual,
                    onValueChange = { nombreManual = it },
                    label = { Text("Nombre de la enfermedad") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = codigoManual,
                    onValueChange = { codigoManual = it },
                    label = { Text("Código ICD-10 (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcionManual,
                    onValueChange = { descripcionManual = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = esRelevanteManual, onCheckedChange = { esRelevanteManual = it })
                    Text("Relevante para la evaluación de signos vitales")
                }
                Button(
                    onClick = {
                        viewModel.agregarManual(
                            nombreEnfermedad = nombreManual,
                            codigoICD10 = codigoManual,
                            descripcion = descripcionManual,
                            esRelevanteSignosVitales = esRelevanteManual
                        )
                        nombreManual = ""
                        codigoManual = ""
                        descripcionManual = ""
                        esRelevanteManual = false
                        mostrarFormularioManual = false
                    },
                    enabled = nombreManual.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar Antecedente")
                }
            }
        }
    }
}

@Composable
    /**
     * ItemAntecedente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun ItemAntecedente(antecedente: AntecedenteEntity, onAnularClick: () -> Unit) {
    val colorTexto = if (antecedente.esAnulado) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val decoracionTexto = if (antecedente.esAnulado) TextDecoration.LineThrough else TextDecoration.None

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val codigo = antecedente.codigoICD10?.let { " (ICD-10: $it)" } ?: ""
                Text(
                    text = "${antecedente.nombreEnfermedad}$codigo",
                    style = MaterialTheme.typography.titleSmall,
                    color = colorTexto,
                    textDecoration = decoracionTexto
                )
                antecedente.descripcion?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = colorTexto, textDecoration = decoracionTexto)
                }
                if (antecedente.esRelevanteSignosVitales && !antecedente.esAnulado) {
                    Text(
                        "Relevante para signos vitales",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (antecedente.esAnulado) {
                    Text(
                        text = "Anulado: ${antecedente.razonAnulacion ?: "Sin razón registrada"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            if (!antecedente.esAnulado) {
                IconButton(onClick = onAnularClick) {
                    Icon(Icons.Default.Block, contentDescription = "Anular antecedente")
                }
            }
        }
    }
}

@Composable
    /**
     * ItemSugerenciaAntecedente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun ItemSugerenciaAntecedente(sugerido: AntecedenteSugerido, onAgregarClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(sugerido.nombreEnfermedad, style = MaterialTheme.typography.bodyMedium)
            sugerido.codigoICD10?.let {
                Text("ICD-10: $it", style = MaterialTheme.typography.labelSmall)
            }
        }
        Button(onClick = onAgregarClick) {
            Text("Agregar")
        }
    }
}

@Composable
    /**
     * ItemAlerta: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun ItemAlerta(alerta: AlertaEntity, onAtenderClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (alerta.nivelSeveridad == "CRITICA")
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "[${alerta.tipoAlerta}] ${alerta.mensaje}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Severidad: ${alerta.nivelSeveridad}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Button(
                onClick = onAtenderClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Atender")
            }
        }
    }
}

@Composable
    /**
     * ItemSignoVital: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun ItemSignoVital(signo: SignoVitalEntity, onAnularClick: () -> Unit) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val fechaFormateada = remember(signo.fechaRegistro) { sdf.format(Date(signo.fechaRegistro)) }
    val colorTexto = if (signo.esAnulado) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val decoracionTexto = if (signo.esAnulado) TextDecoration.LineThrough else TextDecoration.None

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fechaFormateada,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (signo.esAnulado) colorTexto else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!signo.presionArterial.isNullOrBlank()) {
                        Text(text = "PA: ${signo.presionArterial}", style = MaterialTheme.typography.bodyMedium, color = colorTexto, textDecoration = decoracionTexto)
                    }
                    signo.frecuenciaCardiaca?.let { Text(text = "FC: $it bpm", style = MaterialTheme.typography.bodyMedium, color = colorTexto, textDecoration = decoracionTexto) }
                    signo.temperatura?.let { Text(text = "Temp: $it °C", style = MaterialTheme.typography.bodyMedium, color = colorTexto, textDecoration = decoracionTexto) }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    signo.saturacionOxigeno?.let { Text(text = "SpO2: $it%", style = MaterialTheme.typography.bodyMedium, color = colorTexto, textDecoration = decoracionTexto) }
                    signo.dolorEva?.let { Text(text = "EVA: $it/10", style = MaterialTheme.typography.bodyMedium, color = colorTexto, textDecoration = decoracionTexto) }
                }
                if (signo.esAnulado) {
                    Text(
                        text = "Anulado: ${signo.razonAnulacion ?: "Sin razón registrada"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            if (!signo.esAnulado) {
                IconButton(onClick = onAnularClick) {
                    Icon(Icons.Default.Block, contentDescription = "Anular registro")
                }
            }
        }
    }
}

@Composable
    /**
     * SeccionPrescripciones: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun SeccionPrescripciones(
    prescripciones: List<PrescripcionConDetalles>,
    onAplicarDosisClick: (farmacoId: Long, idDetalle: Long, dosis: String, via: String) -> Unit
) {
    // Confirmacion de dosis: se pide doble confirmacion antes de registrar
    // una administracion, para evitar toques accidentales.
    var detalleParaConfirmar by remember { mutableStateOf<DetalleConFarmaco?>(null) }

    Text(text = "Fármacos Prescritos", style = MaterialTheme.typography.titleLarge)

    if (prescripciones.isEmpty()) {
        Text("No hay prescripciones activas para este paciente.", style = MaterialTheme.typography.bodyMedium)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            prescripciones.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Prescripción #${item.prescripcion.idPrescripcion}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        item.detalles.forEach { detalle ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = detalle.farmaco.nombreGenerico, style = MaterialTheme.typography.titleSmall)
                                    Text(text = "Dosis: ${detalle.detalle.dosis} ${detalle.farmaco.unidadMedida}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Vía: ${detalle.detalle.viaAdministracion} · Cada ${detalle.detalle.frecuenciaHoras}h", style = MaterialTheme.typography.bodySmall)
                                }
                                Button(
                                    onClick = { detalleParaConfirmar = detalle },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text("Registrar Dosis")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    detalleParaConfirmar?.let { detalle ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { detalleParaConfirmar = null },
            title = { Text("Confirmar administración") },
            text = {
                Text(
                    "¿Confirma que administró ${detalle.farmaco.nombreGenerico} " +
                        "${detalle.detalle.dosis} ${detalle.farmaco.unidadMedida} vía ${detalle.detalle.viaAdministracion} " +
                        "a este paciente? Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                Button(onClick = {
                    onAplicarDosisClick(
                        detalle.farmaco.idFarmaco,
                        detalle.detalle.idDetalle,
                        detalle.detalle.dosis.toString(),
                        detalle.detalle.viaAdministracion
                    )
                    detalleParaConfirmar = null
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { detalleParaConfirmar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
