package com.example.minisegurancaapp.Security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorage(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun save(nome: String, ra: String) {
        prefs.edit()
            .putString(KEY_NOME, nome)
            .putString(KEY_RA, ra)
            .apply()
    }

    fun load(): Pair<String?, String?> {
        val nome = prefs.getString(KEY_NOME, null)
        val ra = prefs.getString(KEY_RA, null)
        return nome to ra
    }

    companion object {
        private const val PREFS_NAME = "secure_prefs"
        private const val KEY_NOME = "nome"
        private const val KEY_RA = "ra"
    }
}

