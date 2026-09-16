package com.example.clinickcheck.ui.paciente


/**
 * Archivo: com/example/clinickcheck/ui/paciente/SignosVitalesScreen.kt
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import com.example.clinickcheck.local.entity.SignoVitalEntity
import com.example.clinickcheck.ui.theme.EstadoCriticoColor
import com.example.clinickcheck.ui.theme.EstadoCriticoContainer
import com.example.clinickcheck.ui.theme.EstadoEstableColor
import com.example.clinickcheck.ui.theme.EstadoEstableContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
    /**
     * SignosVitalesScreen: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun SignosVitalesScreen(
    onBackClick: () -> Unit,
    viewModel: SignosVitalesViewModel = hiltViewModel()
) {
    val historialSignos by viewModel.historialSignos.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var signoParaAnular by remember { mutableStateOf<SignoVitalEntity?>(null) }

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Signos Vitales") },
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
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Registrar signos vitales")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                historialSignos.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay registros de signos vitales todavía.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(historialSignos, key = { it.idSignoVital }) { signo ->
                            SignosVitalesTarjeta(
                                signo = signo,
                                esAlerta = viewModel.esAlerta(signo),
                                onAnularClick = { signoParaAnular = signo }
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        RegistrarSignosVitalesDialog(
            guardando = uiState.guardando,
            onDismiss = { mostrarDialogo = false },
            onConfirmar = { pa, fc, temp, spo2, eva ->
                viewModel.guardarSignosVitales(pa, fc, temp, spo2, eva)
                mostrarDialogo = false
            }
        )
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
}

/**
 * Tarjeta visual para un registro de signos vitales, con badge de estado
 * (Normal / Alerta) segun si algun valor esta fuera de rango clinico.
 * Inmutabilidad/auditoria: no hay boton de borrado; un registro erroneo se
 * anula (queda visible, atenuado y tachado) mediante onAnularClick.
 */
@Composable
    /**
     * SignosVitalesTarjeta: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun SignosVitalesTarjeta(
    signo: SignoVitalEntity,
    esAlerta: Boolean,
    onAnularClick: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val colorTexto = if (signo.esAnulado) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val decoracionTexto = if (signo.esAnulado) TextDecoration.LineThrough else TextDecoration.None

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sdf.format(Date(signo.fechaRegistro)),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (signo.esAnulado) {
                        Surface(color = EstadoCriticoContainer, shape = RoundedCornerShape(12.dp)) {
                            Text(
                                text = "Anulado",
                                style = MaterialTheme.typography.labelMedium,
                                color = EstadoCriticoColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        BadgeEstadoSignoVital(esAlerta = esAlerta)
                        IconButton(onClick = onAnularClick) {
                            Icon(Icons.Default.Block, contentDescription = "Anular registro")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DatoSignoVital(etiqueta = "PA", valor = signo.presionArterial?.ifBlank { "-" } ?: "-", color = colorTexto, decoracion = decoracionTexto)
                DatoSignoVital(etiqueta = "FC", valor = "${signo.frecuenciaCardiaca ?: "-"} lpm", color = colorTexto, decoracion = decoracionTexto)
                DatoSignoVital(etiqueta = "Temp", valor = "${signo.temperatura ?: "-"} °C", color = colorTexto, decoracion = decoracionTexto)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DatoSignoVital(etiqueta = "SpO2", valor = "${signo.saturacionOxigeno ?: "-"}%", color = colorTexto, decoracion = decoracionTexto)
                DatoSignoVital(etiqueta = "EVA", valor = "${signo.dolorEva ?: "-"}", color = colorTexto, decoracion = decoracionTexto)
                Spacer(modifier = Modifier.width(1.dp))
            }
            if (signo.esAnulado) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Anulado: ${signo.razonAnulacion ?: "Sin razón registrada"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
    /**
     * DatoSignoVital: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun DatoSignoVital(
    etiqueta: String,
    valor: String,
    color: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified,
    decoracion: TextDecoration = TextDecoration.None
) {
    Column {
        Text(text = etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = valor, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = color, textDecoration = decoracion)
    }
}

@Composable
    /**
     * BadgeEstadoSignoVital: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun BadgeEstadoSignoVital(esAlerta: Boolean) {
    val colorFondo = if (esAlerta) EstadoCriticoContainer else EstadoEstableContainer
    val colorTexto = if (esAlerta) EstadoCriticoColor else EstadoEstableColor
    val texto = if (esAlerta) "Alerta" else "Normal"

    Surface(
        color = colorFondo,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            color = colorTexto,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/**
 * Dialogo para registrar un nuevo set de signos vitales. Usa estados locales
 * (remember { mutableStateOf("") }) ya que los valores solo viven mientras
 * el dialogo esta abierto; al confirmar, se delega el guardado al ViewModel.
 */
@Composable
    /**
     * RegistrarSignosVitalesDialog: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun RegistrarSignosVitalesDialog(
    guardando: Boolean,
    onDismiss: () -> Unit,
    onConfirmar: (pa: String, fc: String, temp: String, spo2: String, eva: String) -> Unit
) {
    var pa by remember { mutableStateOf("") }
    var fc by remember { mutableStateOf("") }
    var temp by remember { mutableStateOf("") }
    var spo2 by remember { mutableStateOf("") }
    var eva by remember { mutableStateOf("") }

    // --- Reglas de validacion de rangos (mismos limites que en PacienteDetalleScreen) ---
    val fcValida = fc.isEmpty() || (fc.toIntOrNull() in 30..220)
    val tempValida = temp.isEmpty() || (temp.toDoubleOrNull()?.let { it in 30.0..45.0 } == true)
    val spo2Valida = spo2.isEmpty() || (spo2.toIntOrNull() in 0..100)
    val evaValida = eva.isEmpty() || (eva.toIntOrNull() in 0..10)
    val algunCampoLleno = pa.isNotBlank() || fc.isNotBlank() || temp.isNotBlank() || spo2.isNotBlank() || eva.isNotBlank()
    val formularioValido = algunCampoLleno && fcValida && tempValida && spo2Valida && evaValida

    AlertDialog(
        onDismissRequest = { if (!guardando) onDismiss() },
        title = { Text("Registrar Signos Vitales") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = pa,
                    onValueChange = { pa = it },
                    label = { Text("Presión Arterial (120/80)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fc,
                    onValueChange = { input -> if (input.all { it.isDigit() }) fc = input },
                    label = { Text("Frecuencia Cardíaca (lpm)") },
                    singleLine = true,
                    isError = !fcValida,
                    supportingText = { if (!fcValida) Text("30-220 bpm") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = temp,
                    onValueChange = { input -> if (input.all { it.isDigit() || it == '.' }) temp = input },
                    label = { Text("Temperatura (°C)") },
                    singleLine = true,
                    isError = !tempValida,
                    supportingText = { if (!tempValida) Text("30.0-45.0 °C") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = spo2,
                    onValueChange = { input -> if (input.all { it.isDigit() }) spo2 = input },
                    label = { Text("Saturación de Oxígeno (%)") },
                    singleLine = true,
                    isError = !spo2Valida,
                    supportingText = { if (!spo2Valida) Text("0-100%") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = eva,
                    onValueChange = { input -> if (input.all { it.isDigit() }) eva = input },
                    label = { Text("Dolor EVA (0-10)") },
                    singleLine = true,
                    isError = !evaValida,
                    supportingText = { if (!evaValida) Text("Escala 0 a 10") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmar(pa, fc, temp, spo2, eva) },
                enabled = !guardando && formularioValido
            ) {
                Text(if (guardando) "Guardando..." else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !guardando) {
                Text("Cancelar")
            }
        }
    )
}
