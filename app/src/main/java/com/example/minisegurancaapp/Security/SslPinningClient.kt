package com.example.minisegurancaapp.Security

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class SslPinningClient(
    host: String,
    pinSha256: String
) {
    private val certificatePinner = CertificatePinner.Builder()
        .add(host, pinSha256)
        .build()

    private val httpClient = OkHttpClient.Builder()
        .certificatePinner(certificatePinner)
        .build()

    @Throws(IOException::class)
    fun makePinnedGet(url: String): String {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            return "HTTP ${response.code} (${if (response.isSuccessful) "OK" else "ERRO"})"
        }
    }
}

