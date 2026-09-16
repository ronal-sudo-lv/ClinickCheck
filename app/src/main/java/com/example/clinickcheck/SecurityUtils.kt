package com.example.clinickcheck


/**
 * Archivo: com/example/clinickcheck/SecurityUtils.kt
 * Descripción: Este fichero pertenece a la arquitectura principal de la aplicación y encapsula una parte
 * específica del dominio de la aplicación. Aquí se definen las entidades,
 * flujos de negocio, pantallas, clientes HTTP o utilidades necesarias para
 * que toda la app funcione de forma coherente.
 *
 * El código de este archivo está documentado para facilitar la comprensión
 * del flujo de datos, la persistencia y la navegación del proyecto.
 */
import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Utilidades de seguridad para la aplicación.
 * Incluye gestión de contraseñas de base de datos y hash de contraseñas de usuario.
 */
    /**
     * SecurityUtils: define el comportamiento principal de esta entidad o componente.
     * Su responsabilidad es centralizar la lógica de un dominio concreto.
     */
object SecurityUtils {

    /**
     * Obtiene o crea una frase de paso segura para la base de datos SQLCipher,
     * almacenándola en EncryptedSharedPreferences.
     */
    /**
     * getOrCreateDatabasePassphrase: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun getOrCreateDatabasePassphrase(context: Context): String {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        var passphrase = sharedPreferences.getString("db_passphrase", null)
        if (passphrase == null) {
            passphrase = generateSecurePassphrase()
            sharedPreferences.edit().putString("db_passphrase", passphrase).apply()
        }
        return passphrase
    }

    /**
     * Genera una frase de paso aleatoria y segura.
     */
    /**
     * generateSecurePassphrase: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    private fun generateSecurePassphrase(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    /**
     * Genera un salt aleatorio para el hashing de contraseñas.
     */
    /**
     * generateSalt: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    /**
     * Genera un hash PBKDF2 de una contraseña utilizando un salt.
     */
    /**
     * hashPassword: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun hashPassword(password: String, saltBase64: String): String {
        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
        val spec = PBEKeySpec(password.toCharArray(), salt, 10000, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    /**
     * Verifica si una contraseña coincide con el hash esperado.
     */
    /**
     * verifyPassword: ejecuta la operación asociada a esta acción.
     * Se utiliza para encapsular la lógica reutilizable y mantener el flujo claro.
     */
    fun verifyPassword(password: String, saltBase64: String, expectedHash: String): Boolean {
        val hashToTest = hashPassword(password, saltBase64)
        return hashToTest == expectedHash
    }
}
