# Regras específicas deste app (mínimas).
# Compose/AndroidX normalmente já funciona com o default proguard.

# Mantém as classes usadas por reflection do AndroidX Security (conservador).
-keep class androidx.security.crypto.** { *; }

