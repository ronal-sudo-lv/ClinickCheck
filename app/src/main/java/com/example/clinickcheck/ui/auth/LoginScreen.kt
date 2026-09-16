package com.example.clinickcheck.ui.auth


/**
 * Archivo: com/example/clinickcheck/ui/auth/LoginScreen.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.clinickcheck.local.entity.TurnoTrabajo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
    /**
     * LoginScreen: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var numLicencia by remember { mutableStateOf("") }
    var rolSeleccionado by remember { mutableStateOf("ENFERMERO") }
    var turnoSeleccionado by remember { mutableStateOf(TurnoTrabajo.MANANA) }
    var rolMenuExpandido by remember { mutableStateOf(false) }
    var turnoMenuExpandido by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ClinickCheck",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (isRegistering) "Crear cuenta clínica" else "Ingreso al sistema",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (isRegistering) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = rolMenuExpandido,
                onExpandedChange = { rolMenuExpandido = it }
            ) {
                OutlinedTextField(
                    value = rolSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rol") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = rolMenuExpandido,
                    onDismissRequest = { rolMenuExpandido = false }
                ) {
                    listOf("ENFERMERO", "MEDICO").forEach { rol ->
                        DropdownMenuItem(
                            text = { Text(rol) },
                            onClick = {
                                rolSeleccionado = rol
                                rolMenuExpandido = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = turnoMenuExpandido,
                onExpandedChange = { turnoMenuExpandido = it }
            ) {
                OutlinedTextField(
                    value = turnoSeleccionado.name,
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
                    TurnoTrabajo.entries.forEach { turno ->
                        DropdownMenuItem(
                            text = { Text(turno.name) },
                            onClick = {
                                turnoSeleccionado = turno
                                turnoMenuExpandido = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = numLicencia,
                onValueChange = { numLicencia = it },
                label = { Text("N° Licencia / Registro médico (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (isRegistering) {
                    viewModel.registrar(
                        username = username,
                        password = password,
                        nombre = nombre,
                        apellido = apellido,
                        rol = rolSeleccionado,
                        turno = turnoSeleccionado,
                        numLicencia = numLicencia
                    )
                } else {
                    viewModel.login(username, password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(if (isRegistering) "Registrar" else "Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            isRegistering = !isRegistering
            viewModel.resetState()
        }) {
            Text(if (isRegistering) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate")
        }
    }
}
