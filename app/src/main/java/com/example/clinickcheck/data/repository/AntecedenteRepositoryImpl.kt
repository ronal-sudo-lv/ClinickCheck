package com.example.clinickcheck.data.repository


/**
 * Archivo: com/example/clinickcheck/data/repository/AntecedenteRepositoryImpl.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import com.example.clinickcheck.data.remote.TranslationApi
import com.example.clinickcheck.local.dao.AntecedenteDao
import com.example.clinickcheck.local.entity.AntecedenteEntity
import com.example.clinickcheck.remote.ClinicalTablesApi
import com.example.clinickcheck.remote.dto.ClinicalTablesResponse
import com.example.clinickcheck.util.ConnectivityHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.util.Locale
import javax.inject.Inject

/**
 * Implementación de [AntecedenteRepository] que coordina el almacenamiento local y la API remota.
 */
    /**
     * AntecedenteRepositoryImpl: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class AntecedenteRepositoryImpl @Inject constructor(
    private val antecedenteDao: AntecedenteDao,
    private val clinicalTablesApi: ClinicalTablesApi,
    private val translationApi: TranslationApi,
    private val connectivityHelper: ConnectivityHelper
) : AntecedenteRepository {

    /**
     * getAntecedentesPorPaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun getAntecedentesPorPaciente(pacienteId: Long): Flow<List<AntecedenteEntity>> {
        return antecedenteDao.getAntecedentesPorPaciente(pacienteId)
    }

    /**
     * getAntecedentesPorPacienteOnce: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun getAntecedentesPorPacienteOnce(pacienteId: Long): List<AntecedenteEntity> = withContext(Dispatchers.IO) {
        antecedenteDao.getAntecedentesPorPacienteOnce(pacienteId)
    }

    /**
     * insertarAntecedente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun insertarAntecedente(antecedente: AntecedenteEntity): Long = withContext(Dispatchers.IO) {
        antecedenteDao.insertAntecedente(antecedente)
    }

    /**
     * anularAntecedente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun anularAntecedente(idAntecedente: Long, razon: String) = withContext(Dispatchers.IO) {
        antecedenteDao.anularAntecedente(idAntecedente, razon)
    }

    /**
     * hayConexionInternet: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override fun hayConexionInternet(): Boolean = connectivityHelper.hayConexionInternet()

    /**
     * normalizarTerminoParaClinicalTables: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private fun normalizarTerminoParaClinicalTables(term: String): String {
        val base = term.trim().ifBlank { return "" }
        val sinAcentos = buildString {
            base.lowercase(Locale.getDefault()).forEach { char ->
                append(
                    when (char) {
                        'á', 'à', 'ä' -> 'a'
                        'é', 'è', 'ë' -> 'e'
                        'í', 'ì', 'ï' -> 'i'
                        'ó', 'ò', 'ö' -> 'o'
                        'ú', 'ù', 'ü' -> 'u'
                        'ñ' -> 'n'
                        else -> char
                    }
                )
            }
        }
            .replace(Regex("\\s+"), " ")
            .trim()

        return when (sinAcentos) {
            "cancer", "cáncer", "carcinoma" -> "cancer"
            "leucemia", "leukemia" -> "leukemia"
            "diabetes" -> "diabetes"
            "hipertension", "hypertension" -> "hypertension"
            "miocardio", "cardiopathy", "cardiopatia" -> "cardiopathy"
            else -> sinAcentos
        }
    }

    /**
     * traducirNombreEnfermedad: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private suspend fun traducirNombreEnfermedad(nombre: String): String {
        val texto = nombre.trim()
        if (texto.isBlank()) return nombre

        return try {
            val respuesta = translationApi.translate(q = texto, langpair = "en|es")
            val traducido = respuesta.responseData?.translatedText
                ?: respuesta.matches.firstOrNull()?.translation
                ?: texto
            traducido.trim().ifBlank { texto }
        } catch (_: Exception) {
            texto
        }
    }

    /**
     * buscarEnApi: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    override suspend fun buscarEnApi(query: String): Result<List<AntecedenteSugerido>> = withContext(Dispatchers.IO) {
        val terminoOriginal = query.trim()
        if (terminoOriginal.isBlank()) return@withContext Result.success(emptyList())

        if (!connectivityHelper.hayConexionInternet()) {
            return@withContext Result.failure(Exception("Modo sin conexión: ingrese el antecedente manualmente."))
        }

        try {
            val terminoApi = normalizarTerminoParaClinicalTables(terminoOriginal)
            val responseJson = clinicalTablesApi.searchIcd10(query = terminoApi)
            val model = ClinicalTablesResponse.fromJsonElement(responseJson)
            val sugerencias = model.displayResults.mapNotNull { fila ->
                val codigo = fila.getOrNull(0)
                val nombre = fila.getOrNull(1)
                if (nombre.isNullOrBlank()) null
                else {
                    val nombreTraducido = traducirNombreEnfermedad(nombre)
                    AntecedenteSugerido(nombreEnfermedad = nombreTraducido, codigoICD10 = codigo)
                }
            }
            Result.success(sugerencias)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Sin conexión a internet. Registra el antecedente manualmente"))
        } catch (e: Exception) {
            Result.failure(Exception("Error al conectar con el servicio: ${e.message}"))
        }
    }
}
