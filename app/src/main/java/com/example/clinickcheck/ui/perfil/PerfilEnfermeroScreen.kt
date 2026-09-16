package com.example.clinickcheck.ui.perfil


/**
 * Archivo: com/example/clinickcheck/ui/perfil/PerfilEnfermeroScreen.kt
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.clinickcheck.local.entity.TurnoTrabajo
import com.example.clinickcheck.local.entity.UsuarioEntity
import com.example.clinickcheck.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
    /**
     * PerfilEnfermeroScreen: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun PerfilEnfermeroScreen(
    onBackClick: () -> Unit,
    viewModel: PerfilViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val guardadoExitoso by viewModel.guardadoExitoso.collectAsState()
    val resumenTurno by viewModel.resumenTurno.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val cerrandoSesion by viewModel.cerrandoSesion.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var mostrarDialogoNuevaCuenta by remember { mutableStateOf(false) }
    var mostrarDialogoConfiguracion by remember { mutableStateOf(false) }

    LaunchedEffect(guardadoExitoso) {
        if (guardadoExitoso) {
            snackbarHostState.showSnackbar("Perfil actualizado correctamente")
            viewModel.resetGuardadoExitoso()
        }
    }

    // Unico punto de Snackbar: tanto los mensajes informativos como los de
    // guardado pasan por aqui, en secuencia, para que nunca se solapen.
    LaunchedEffect(mensaje) {
        // Mientras se esta cerrando sesion no se muestran mensajes nuevos:
        // la pantalla esta a punto de desmontarse hacia Login.
        if (!cerrandoSesion) {
            mensaje?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.mensajeMostrado()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
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
        }
    ) { padding ->
        when (val estado = uiState) {
            is PerfilUiState.Cargando -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PerfilUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(estado.mensaje, color = MaterialTheme.colorScheme.error)
                }
            }
            is PerfilUiState.Listo -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaCabecera(usuario = estado.usuario)

                    TarjetaIdentificacionProfesional(usuario = estado.usuario)

                    TarjetaResumenTurno(resumen = resumenTurno)

                    TarjetaAjustesYSoporte(
                        onAgregarOCambiarCuentaClick = { mostrarDialogoNuevaCuenta = true },
                        onConfiguracionCuentaClick = { mostrarDialogoConfiguracion = true },
                        onGuiasClick = {
                            viewModel.mostrarInfo("Función en desarrollo")
                        },
                        onCentroAyudaClick = {
                            viewModel.mostrarInfo("Función en desarrollo")
                        },
                        onAcercaDeClick = {
                            viewModel.mostrarInfo("ClinickCheck — Gestión clínica de pacientes")
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            viewModel.iniciarCierreSesion { authViewModel.logout() }
                        },
                        enabled = !cerrandoSesion,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (cerrandoSesion) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cerrar sesión")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (mostrarDialogoNuevaCuenta) {
                    AgregarOCambiarCuentaDialog(
                        onDismiss = { mostrarDialogoNuevaCuenta = false },
                        onConfirmar = { nombreCompleto, legajo, rol ->
                            viewModel.crearOCambiarCuentaRapida(nombreCompleto, legajo, rol)
                            mostrarDialogoNuevaCuenta = false
                        }
                    )
                }

                if (mostrarDialogoConfiguracion) {
                    ConfiguracionCuentaDialog(
                        usuario = estado.usuario,
                        onDismiss = { mostrarDialogoConfiguracion = false },
                        onGuardar = { nombre, apellido, numLicencia, turno ->
                            viewModel.guardarCambios(nombre, apellido, numLicencia, turno)
                            mostrarDialogoConfiguracion = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
    /**
     * TarjetaCabecera: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun TarjetaCabecera(usuario: UsuarioEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iniciales = "${usuario.nombre.firstOrNull() ?: ' '}${usuario.apellido.firstOrNull() ?: ' '}".uppercase()
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${usuario.nombre} ${usuario.apellido}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Hospital ClinickCheck",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(6.dp))
                AssistChip(
                    onClick = {},
                    label = { Text("Turno ${usuario.turno.name} · Sala 3") }
                )
            }
        }
    }
}

/**
 * Tarjeta "IDENTIFICACIÓN PROFESIONAL". Cada fila usa un Row con una columna
 * de etiqueta de ancho fijo y una columna de valor con Modifier.weight(1f),
 * de forma que valores largos (por ejemplo un rol descriptivo como
 * "Estudiante de enfermería — Práctica clínica") se acomodan a la derecha
 * y ya no se superponen con el título de la etiqueta.
 */
@Composable
    /**
     * TarjetaIdentificacionProfesional: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun TarjetaIdentificacionProfesional(usuario: UsuarioEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "IDENTIFICACIÓN PROFESIONAL",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            FilaIdentificacion(etiqueta = "Rol", valor = usuario.rol)
            Spacer(modifier = Modifier.height(10.dp))
            FilaIdentificacion(
                etiqueta = "Legajo/Matrícula",
                valor = usuario.numLicencia?.takeIf { it.isNotBlank() } ?: "No registrado"
            )
            Spacer(modifier = Modifier.height(10.dp))
            FilaIdentificacion(etiqueta = "Supervisor a cargo", valor = "Por asignar")
        }
    }
}

@Composable
    /**
     * FilaIdentificacion: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun FilaIdentificacion(etiqueta: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(130.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
    /**
     * TarjetaResumenTurno: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun TarjetaResumenTurno(resumen: ResumenTurno) {
    Column {
        Text(
            text = "RESUMEN DEL TURNO",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ContadorResumen(
                valor = resumen.pacientesAtendidos,
                etiqueta = "Pacientes atendidos",
                modifier = Modifier.weight(1f)
            )
            ContadorResumen(
                valor = resumen.signosVitalesRegistrados,
                etiqueta = "Signos vitales registrados",
                modifier = Modifier.weight(1f)
            )
            ContadorResumen(
                valor = resumen.dosisAdministradas,
                etiqueta = "Dosis administradas",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
    /**
     * ContadorResumen: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun ContadorResumen(valor: Int, etiqueta: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
    /**
     * TarjetaAjustesYSoporte: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun TarjetaAjustesYSoporte(
    onAgregarOCambiarCuentaClick: () -> Unit,
    onConfiguracionCuentaClick: () -> Unit,
    onGuiasClick: () -> Unit,
    onCentroAyudaClick: () -> Unit,
    onAcercaDeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = "AJUSTES Y SOPORTE",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            FilaAjuste(
                icono = Icons.Default.PersonAddAlt,
                texto = "Agregar o cambiar cuenta",
                onClick = onAgregarOCambiarCuentaClick
            )
            Divider()
            FilaAjuste(texto = "Configuración de cuenta", onClick = onConfiguracionCuentaClick)
            Divider()
            FilaAjuste(texto = "Guías de práctica clínica", onClick = onGuiasClick)
            Divider()
            FilaAjuste(texto = "Centro de ayuda", onClick = onCentroAyudaClick)
            Divider()
            FilaAjuste(texto = "Acerca de ClinickCheck", onClick = onAcercaDeClick)
        }
    }
}

@Composable
    /**
     * FilaAjuste: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun FilaAjuste(
    icono: androidx.compose.ui.graphics.vector.ImageVector? = null,
    texto: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icono != null) {
                Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(text = texto, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Dialogo de registro rapido ("Agregar o cambiar cuenta"): pide solo 3
 * campos y usa estados locales, ya que los valores solo viven mientras el
 * dialogo esta abierto.
 */
@Composable
    /**
     * AgregarOCambiarCuentaDialog: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun AgregarOCambiarCuentaDialog(
    onDismiss: () -> Unit,
    onConfirmar: (nombreCompleto: String, legajo: String, rol: String) -> Unit
) {
    var nombreCompleto by remember { mutableStateOf("") }
    var legajo by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar o cambiar cuenta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Crea una cuenta local rápida para cambiar de usuario en este dispositivo, sin pasar por el inicio de sesión completo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = nombreCompleto,
                    onValueChange = { nombreCompleto = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = legajo,
                    onValueChange = { legajo = it },
                    label = { Text("Legajo / Matrícula") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rol,
                    onValueChange = { rol = it },
                    label = { Text("Rol (ej. Estudiante / Enfermero)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmar(nombreCompleto, legajo, rol) },
                enabled = nombreCompleto.isNotBlank()
            ) {
                Text("Crear / Cambiar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
    /**
     * ConfiguracionCuentaDialog: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun ConfiguracionCuentaDialog(
    usuario: UsuarioEntity,
    onDismiss: () -> Unit,
    onGuardar: (nombre: String, apellido: String, numLicencia: String?, turno: TurnoTrabajo) -> Unit
) {
    var nombre by remember { mutableStateOf(usuario.nombre) }
    var apellido by remember { mutableStateOf(usuario.apellido) }
    var numLicencia by remember { mutableStateOf(usuario.numLicencia ?: "") }
    var turno by remember { mutableStateOf(usuario.turno) }
    var turnoMenuExpandido by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configuración de cuenta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = numLicencia,
                    onValueChange = { numLicencia = it },
                    label = { Text("N° Licencia / Registro médico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(
                    expanded = turnoMenuExpandido,
                    onExpandedChange = { turnoMenuExpandido = it }
                ) {
                    OutlinedTextField(
                        value = turno.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Turno") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = turnoMenuExpandido,
                        onDismissRequest = { turnoMenuExpandido = false }
                    ) {
                        TurnoTrabajo.entries.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion.name) },
                                onClick = {
                                    turno = opcion
                                    turnoMenuExpandido = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onGuardar(nombre, apellido, numLicencia, turno) },
                enabled = nombre.isNotBlank() && apellido.isNotBlank()
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
