package com.example.clinickcheck.ui.navigation


/**
 * Archivo: com/example/clinickcheck/ui/navigation/AppNavigation.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.clinickcheck.local.entity.EstadoPaciente
import com.example.clinickcheck.ui.auth.MainViewModel
import com.example.clinickcheck.ui.auth.LoginScreen
import com.example.clinickcheck.ui.inicio.InicioScreen
import com.example.clinickcheck.ui.inicio.InicioViewModel
import com.example.clinickcheck.ui.inicio.SelectorPacienteScreen
import com.example.clinickcheck.ui.paciente.AgregarPacienteScreen
import com.example.clinickcheck.ui.paciente.HistorialDosisScreen
import com.example.clinickcheck.ui.paciente.MedicamentosScreen
import com.example.clinickcheck.ui.paciente.PacienteDetalleScreen
import com.example.clinickcheck.ui.paciente.PacienteViewModel
import com.example.clinickcheck.ui.paciente.PacientesScreen
import com.example.clinickcheck.ui.paciente.SignosVitalesScreen
import com.example.clinickcheck.ui.perfil.PerfilEnfermeroScreen

sealed class Screen(val route: String) {
    /**
     * Login: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    object Login : Screen("login")
    /**
     * Main: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    object Main : Screen("main")
    /**
     * PacientesList: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    object PacientesList : Screen("pacientes_list")
    /**
     * RegistroPaciente: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    object RegistroPaciente : Screen("registro_paciente")
    /**
     * Perfil: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    object Perfil : Screen("perfil")
    /**
     * PacienteDetalle: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    object PacienteDetalle : Screen("paciente_detalle/{pacienteId}") {
    /**
     * createRoute: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
        fun createRoute(pacienteId: Long) = "paciente_detalle/$pacienteId"
    }
    /**
     * HistorialDosis: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    object HistorialDosis : Screen("historial_dosis/{pacienteId}") {
    /**
     * createRoute: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
        fun createRoute(pacienteId: Long) = "historial_dosis/$pacienteId"
    }
    /**
     * SignosVitales: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    object SignosVitales : Screen("signos_vitales/{pacienteId}") {
    /**
     * createRoute: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
        fun createRoute(pacienteId: Long) = "signos_vitales/$pacienteId"
    }
    /**
     * Medicamentos: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
    object Medicamentos : Screen("medicamentos/{pacienteId}") {
    /**
     * createRoute: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
        fun createRoute(pacienteId: Long) = "medicamentos/$pacienteId"
    }
}

@Composable
    /**
     * AppNavigation: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()

    // Mientras no se conoce el estado real de la sesion (null), se muestra un
    // indicador de carga en vez de decidir un destino de forma prematura.
    if (isLoggedIn == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true && navController.currentDestination?.route == Screen.Login.route) {
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        } else if (isLoggedIn == false && navController.currentDestination?.route != Screen.Login.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn == true) Screen.Main.route else Screen.Login.route
    ) {
        // 0. Pantalla de Login
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 1. Contenedor principal con Bottom Navigation Bar (Inicio / Signos
        // Vitales / Medicamentos / Perfil). Es el destino de entrada tras el login.
        composable(route = Screen.Main.route) {
            MainScaffold(navController = navController)
        }

        // 1.b Pantalla completa de lista de pacientes (búsqueda, filtros y
        // dashboard). Sigue existiendo tal cual funcionaba antes; se llega a
        // ella desde "Ver todos" en Inicio o desde el icono de perfil previo.
        composable(route = Screen.PacientesList.route) {
            PacientesScreen(
                viewModel = hiltViewModel(),
                onPacienteClick = { pacienteId ->
                    navController.navigate(Screen.PacienteDetalle.createRoute(pacienteId))
                },
                onAgregarPacienteClick = {
                    navController.navigate(Screen.RegistroPaciente.route)
                },
                onPerfilClick = {
                    navController.navigate(Screen.Perfil.route)
                }
            )
        }

        composable(route = "pacientes_criticos") {
            val viewModel: PacienteViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                viewModel.onEstadoFiltroChanged(EstadoPaciente.CRITICO)
            }
            PacientesScreen(
                viewModel = viewModel,
                onPacienteClick = { pacienteId ->
                    navController.navigate(Screen.PacienteDetalle.createRoute(pacienteId))
                },
                onAgregarPacienteClick = {
                    navController.navigate(Screen.RegistroPaciente.route)
                },
                onPerfilClick = {
                    navController.navigate(Screen.Perfil.route)
                }
            )
        }

        // 1.c Pantalla de Perfil del Enfermero (tambien accesible como
        // pantalla completa fuera de la pestaña de Perfil, p. ej. desde
        // PacientesScreen).
        composable(route = Screen.Perfil.route) {
            PerfilEnfermeroScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // 2. Pantalla Registro de Paciente
        composable(route = Screen.RegistroPaciente.route) {
            AgregarPacienteScreen(
                onGuardarExitoso = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Pantalla Detalle del Paciente
        composable(
            route = Screen.PacienteDetalle.route,
            arguments = listOf(
                navArgument("pacienteId") { type = NavType.LongType }
            )
        ) {
            PacienteDetalleScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onVerHistorialDosisClick = { pacienteId ->
                    navController.navigate(Screen.HistorialDosis.createRoute(pacienteId))
                }
            )
        }

        // 3.b Pantalla Historial / Auditoría de Dosis
        composable(
            route = Screen.HistorialDosis.route,
            arguments = listOf(
                navArgument("pacienteId") { type = NavType.LongType }
            )
        ) {
            HistorialDosisScreen(
                viewModel = hiltViewModel(),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // 4. Pantalla de Signos Vitales
        // NOTA: aqui pacienteId viaja como String (NavType.StringType) y se
        // convierte de forma segura a Long dentro de SignosVitalesViewModel,
        // que es donde realmente se necesita el tipo numerico para consultar
        // la base de datos. Este modulo es independiente del de Medicamentos:
        // no comparte ViewModel, DAO ni logica con el.
        composable(
            route = Screen.SignosVitales.route,
            arguments = listOf(
                navArgument("pacienteId") { type = NavType.StringType }
            )
        ) {
            SignosVitalesScreen(
                viewModel = hiltViewModel(),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // 5. Pantalla de Control de Medicamentos
        // NOTA: mismo criterio que en SignosVitales: pacienteId viaja como
        // String y MedicamentosViewModel hace el toLongOrNull() de forma segura.
        // Este modulo es independiente del de Signos Vitales.
        composable(
            route = Screen.Medicamentos.route,
            arguments = listOf(
                navArgument("pacienteId") { type = NavType.StringType }
            )
        ) {
            MedicamentosScreen(
                viewModel = hiltViewModel(),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/** Las 4 pestañas fijas de la barra de navegación inferior. */
    /**
     * TabPrincipal: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
private enum class TabPrincipal(val etiqueta: String, val icono: ImageVector, val rutaRaiz: String) {
    INICIO("Inicio", Icons.Default.Home, "inicio_tab"),
    SIGNOS_VITALES("Signos Vitales", Icons.Default.Favorite, "signos_vitales_selector"),
    MEDICAMENTOS("Medicamentos", Icons.Default.Medication, "medicamentos_selector"),
    PERFIL("Perfil", Icons.Default.Person, "perfil_tab")
}

/**
 * Contenedor principal con la Bottom Navigation Bar. A diferencia de un
 * enfoque con un dialogo "recordado" a mano (remember { mutableStateOf(...) }),
 * aqui cada pestaña -incluidas "Signos Vitales" y "Medicamentos"- es un
 * destino real dentro de un NavHost interno propio de este contenedor. Esto
 * corrige de raiz el bug de navegacion: al cambiar de pestaña, el
 * NavController interno simplemente navega a un destino estable (con
 * popUpTo a la raiz de esa pestaña), por lo que nunca puede quedar un
 * dialogo modal "atrapado" reapareciendo en otra pestaña.
 *
 * "Signos Vitales" y "Medicamentos" siguen siendo modulos completamente
 * independientes entre si: cada uno tiene su propia pantalla selectora y su
 * propia ruta con pacienteId, sin compartir ViewModel ni DAO.
 */
@Composable
    /**
     * MainScaffold: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
private fun MainScaffold(navController: NavHostController) {
    val innerNavController = rememberNavController()
    val innerBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = innerBackStackEntry?.destination?.route

    // Se resuelve una unica vez por contenedor (Hilt cachea la instancia por
    // el mismo NavBackStackEntry) y alimenta tanto la pestaña de Inicio como
    // las pantallas selectoras de Signos Vitales/Medicamentos.
    val inicioViewModel: InicioViewModel = hiltViewModel()
    val misPacientes by inicioViewModel.misPacientes.collectAsState()

    /**
     * tabSeleccionActual: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun tabSeleccionActual(): TabPrincipal = when {
        currentRoute == TabPrincipal.SIGNOS_VITALES.rutaRaiz || currentRoute == "signos_vitales_tab/{pacienteId}" -> TabPrincipal.SIGNOS_VITALES
        currentRoute == TabPrincipal.MEDICAMENTOS.rutaRaiz || currentRoute == "medicamentos_tab/{pacienteId}" -> TabPrincipal.MEDICAMENTOS
        currentRoute == TabPrincipal.PERFIL.rutaRaiz -> TabPrincipal.PERFIL
        else -> TabPrincipal.INICIO
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                TabPrincipal.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tabSeleccionActual() == tab,
                        onClick = {
                            innerNavController.navigate(tab.rutaRaiz) {
                                popUpTo(innerNavController.graph.findStartDestination().id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(tab.icono, contentDescription = tab.etiqueta) },
                        label = { Text(tab.etiqueta) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = innerNavController,
                startDestination = TabPrincipal.INICIO.rutaRaiz
            ) {
                composable(route = TabPrincipal.INICIO.rutaRaiz) {
                    InicioScreen(
                        viewModel = inicioViewModel,
                        onRegistrarVitalesClick = { pacienteId ->
                            navController.navigate(Screen.SignosVitales.createRoute(pacienteId))
                        },
                        onAplicarDosisClick = { pacienteId ->
                            navController.navigate(Screen.Medicamentos.createRoute(pacienteId))
                        },
                        onNuevoPacienteClick = {
                            navController.navigate(Screen.RegistroPaciente.route)
                        },
                        onPacienteClick = { pacienteId ->
                            navController.navigate(Screen.PacienteDetalle.createRoute(pacienteId))
                        },
                        onVerTodosLosPacientesClick = {
                            navController.navigate(Screen.PacientesList.route)
                        },
                        onNavigateToPacientesCriticos = {
                            navController.navigate("pacientes_criticos")
                        },
                        onNavigateToDosisPendientes = {
                            innerNavController.navigate(TabPrincipal.MEDICAMENTOS.rutaRaiz)
                        }
                    )
                }

                composable(route = TabPrincipal.PERFIL.rutaRaiz) {
                    PerfilEnfermeroScreen(
                        onBackClick = {
                            innerNavController.navigate(TabPrincipal.INICIO.rutaRaiz) {
                                popUpTo(innerNavController.graph.findStartDestination().id) { inclusive = false }
                            }
                        }
                    )
                }

                // Raiz de la pestaña "Signos Vitales": selecciona el
                // paciente y navega, con el NavController EXTERNO, hacia la
                // pantalla real e independiente del modulo.
                composable(route = TabPrincipal.SIGNOS_VITALES.rutaRaiz) {
                    SelectorPacienteScreen(
                        titulo = "Signos Vitales — Selecciona un paciente",
                        pacientes = misPacientes.map { it.paciente },
                        onSeleccionar = { pacienteId ->
                            navController.navigate(Screen.SignosVitales.createRoute(pacienteId))
                        }
                    )
                }

                // Raiz de la pestaña "Medicamentos": mismo criterio,
                // totalmente independiente de Signos Vitales.
                composable(route = TabPrincipal.MEDICAMENTOS.rutaRaiz) {
                    SelectorPacienteScreen(
                        titulo = "Medicamentos — Selecciona un paciente",
                        pacientes = misPacientes.map { it.paciente },
                        onSeleccionar = { pacienteId ->
                            navController.navigate(Screen.Medicamentos.createRoute(pacienteId))
                        }
                    )
                }
            }
        }
    }
}
