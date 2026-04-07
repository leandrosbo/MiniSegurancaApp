package com.example.minisegurancaapp.Security

import android.os.Build
import java.io.File

object RootDetector {
    fun isDeviceRooted(): Boolean {
        return checkBuildTags() || checkSuPaths() || checkDangerousProps()
    }

    private fun checkBuildTags(): Boolean {
        val tags = Build.TAGS ?: return false
        return tags.contains("test-keys")
    }

    private fun checkSuPaths(): Boolean {
        val paths = listOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        return paths.any { File(it).exists() }
    }

    private fun checkDangerousProps(): Boolean {
        // Heurística simples: emulados/ROMs costumam expor isso; não é definitivo
        val fingerprint = Build.FINGERPRINT ?: return false
        val model = Build.MODEL ?: ""
        return fingerprint.contains("generic", ignoreCase = true) ||
            model.contains("google_sdk", ignoreCase = true) ||
            model.contains("Emulator", ignoreCase = true) ||
            model.contains("Android SDK built for x86", ignoreCase = true)
    }
}

