package com.example.clinickcheck.ui.paciente


/**
 * Archivo: com/example/clinickcheck/ui/paciente/AgregarPacienteScreen.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.clinickcheck.local.entity.EstadoPaciente

@OptIn(ExperimentalMaterial3Api::class)
@Composable
    /**
     * AgregarPacienteScreen: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun AgregarPacienteScreen(
    onGuardarExitoso: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: PacienteViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var cama by remember { mutableStateOf("") }
    var estadoSeleccionado by remember { mutableStateOf(EstadoPaciente.ESTABLE) }
    var estadoMenuExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Paciente") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dni,
                onValueChange = { dni = it },
                label = { Text("DNI / Documento") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cama,
                onValueChange = { cama = it },
                label = { Text("Cama (Ej: Cama 12)") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = estadoMenuExpandido,
                onExpandedChange = { estadoMenuExpandido = it }
            ) {
                OutlinedTextField(
                    value = estadoSeleccionado.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado Clínico") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = estadoMenuExpandido,
                    onDismissRequest = { estadoMenuExpandido = false }
                ) {
                    EstadoPaciente.entries.forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado.name) },
                            onClick = {
                                estadoSeleccionado = estado
                                estadoMenuExpandido = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.registrarPaciente(
                        nombre = nombre,
                        apellido = apellido,
                        dni = dni,
                        cama = cama,
                        fechaNacimiento = 0L, // Valor por defecto
                        sexo = "M",           // Valor por defecto
                        estado = estadoSeleccionado,
                        onResult = { success, _ ->
                            if (success) onGuardarExitoso()
                        }
                    )
                },
                enabled = nombre.isNotBlank() && apellido.isNotBlank() && dni.isNotBlank() && cama.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Guardar Paciente")
            }
        }
    }
}
