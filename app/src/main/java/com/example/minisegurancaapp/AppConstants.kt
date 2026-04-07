package com.example.minisegurancaapp

object AppConstants {
    // TODO: substitua pelos seus dados
    const val NOME = "LEANDRO BORGES"
    const val RA = "CV3099016"

    // SSL pinning: configure host e pin SHA-256 do certificado público.
    // Formato do pin: "sha256/BASE64_DO_HASH"
    // Exemplo didático (troque por um pin real do seu host):
    // openssl s_client -servername seu-host -connect seu-host:443 < /dev/null 2>/dev/null
    // | openssl x509 -pubkey -noout
    // | openssl pkey -pubin -outform der
    // | openssl dgst -sha256 -binary
    // | openssl enc -base64
    const val PINNED_HOST = "api.github.com"
    const val PINNED_SHA256 = "sha256/tt9RksdSBGiieTiyWkU8g3MOmCrfMcvXDGC4ZALs9rg="
    const val PINNED_URL  = "https://api.github.com/"
}

