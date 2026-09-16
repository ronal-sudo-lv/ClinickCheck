package com.example.clinickcheck


/**
 * Archivo: com/example/clinickcheck/PdfGenerator.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.clinickcheck.local.entity.AntecedenteEntity
import com.example.clinickcheck.local.entity.PacienteEntity
import com.example.clinickcheck.local.entity.SignoVitalEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

    /**
     * PdfGenerator: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
class PdfGenerator(private val context: Context) {

    private val anchoPagina = 595
    private val altoPagina = 842
    private val margenIzquierdo = 50f
    private val margenDerecho = 545f

    /**
     * generarReportePaciente: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun generarReportePaciente(
        paciente: PacienteEntity,
        historialSignos: List<SignoVitalEntity>,
        antecedentes: List<AntecedenteEntity> = emptyList(),
        nombreEnfermeroResponsable: String = "No identificado",
        licenciaEnfermeroResponsable: String? = null
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(anchoPagina, altoPagina, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paintMembrete = Paint().apply {
            color = Color.rgb(10, 77, 104) // #0A4D68 - Azul Médico Profundo
            textSize = 20f
            isFakeBoldText = true
        }
        val paintSubMembrete = Paint().apply {
            color = Color.rgb(8, 131, 149) // #088395 - Turquesa Clínico
            textSize = 12f
        }
        val paintSeccion = Paint().apply {
            color = Color.DKGRAY
            textSize = 14f
            isFakeBoldText = true
        }
        val paintTexto = Paint().apply {
            color = Color.BLACK
            textSize = 12f
        }
        val paintTextoChico = Paint().apply {
            color = Color.DKGRAY
            textSize = 10f
        }
        val paintLinea = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        var y = 45f

        // --- MEMBRETE INSTITUCIONAL ---
        canvas.drawText("Reporte Clínico Hospitalario - ClinickCheck", margenIzquierdo, y, paintMembrete)
        y += 18f
        canvas.drawText(
            "Documento generado electrónicamente por el sistema ClinickCheck",
            margenIzquierdo, y, paintSubMembrete
        )
        y += 14f
        canvas.drawLine(margenIzquierdo, y, margenDerecho, y, paintLinea)
        y += 20f

        val sdfCompleto = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        canvas.drawText("Fecha y hora de emisión: ${sdfCompleto.format(Date())}", margenIzquierdo, y, paintTexto)
        y += 30f

        // --- DATOS DEL PACIENTE ---
        canvas.drawText("Información General del Paciente", margenIzquierdo, y, paintSeccion)
        y += 20f
        canvas.drawText("Nombre: ${paciente.nombre} ${paciente.apellido}", margenIzquierdo, y, paintTexto)
        y += 18f
        canvas.drawText("DNI: ${paciente.dni}    |    Cama: ${paciente.cama}", margenIzquierdo, y, paintTexto)
        y += 18f
        canvas.drawText("Estado clínico: ${paciente.estado}", margenIzquierdo, y, paintTexto)
        y += 18f
        if (!paciente.alergias.isNullOrBlank()) {
            canvas.drawText("Alergias: ${paciente.alergias}", margenIzquierdo, y, paintTexto)
            y += 18f
        }
        y += 15f

        // --- ANTECEDENTES MÉDICOS ---
        canvas.drawText("Antecedentes Médicos", margenIzquierdo, y, paintSeccion)
        y += 20f
        if (antecedentes.isEmpty()) {
            canvas.drawText("No hay antecedentes médicos registrados.", margenIzquierdo, y, paintTexto)
            y += 18f
        } else {
            antecedentes.forEach { antecedente ->
                val codigo = antecedente.codigoICD10?.let { " (ICD-10: $it)" } ?: ""
                canvas.drawText("• ${antecedente.nombreEnfermedad}$codigo", margenIzquierdo, y, paintTexto)
                y += 16f
                if (!antecedente.descripcion.isNullOrBlank()) {
                    canvas.drawText("   ${antecedente.descripcion}", margenIzquierdo, y, paintTextoChico)
                    y += 16f
                }
            }
        }
        y += 15f

        // --- RESUMEN DE SIGNOS VITALES ---
        canvas.drawText("Resumen de Signos Vitales (últimos registros)", margenIzquierdo, y, paintSeccion)
        y += 20f

        if (historialSignos.isEmpty()) {
            canvas.drawText("No hay registros disponibles.", margenIzquierdo, y, paintTexto)
            y += 18f
        } else {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            historialSignos.take(10).forEach { signo ->
                val fecha = sdf.format(Date(signo.fechaRegistro))
                val resumen = "$fecha | PA: ${signo.presionArterial?.ifBlank { "-" } ?: "-"} | FC: ${signo.frecuenciaCardiaca ?: "-"} bpm | " +
                    "Temp: ${signo.temperatura ?: "-"} °C | SpO2: ${signo.saturacionOxigeno ?: "-"}% | EVA: ${signo.dolorEva ?: "-"}"
                canvas.drawText(resumen, margenIzquierdo, y, paintTextoChico)
                y += 16f
            }
        }

        // --- BLOQUE DE FIRMA (se ubica cerca del pie de página) ---
        val yFirma = altoPagina - 110f
        canvas.drawLine(margenIzquierdo, yFirma, margenDerecho, yFirma, paintLinea)
        canvas.drawText("Firma Digital / Registro del Enfermero Responsable", margenIzquierdo, yFirma + 20f, paintSeccion)
        canvas.drawText("Nombre: $nombreEnfermeroResponsable", margenIzquierdo, yFirma + 40f, paintTexto)
        canvas.drawText(
            "N° Licencia: ${licenciaEnfermeroResponsable ?: "No registrada"}",
            margenIzquierdo, yFirma + 58f, paintTexto
        )
        canvas.drawText(
            "Documento validado electrónicamente por ClinickCheck el ${sdfCompleto.format(Date())}",
            margenIzquierdo, yFirma + 78f, paintTextoChico
        )

        pdfDocument.finishPage(page)

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ClinickCheck_${paciente.dni}.pdf")

        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}
