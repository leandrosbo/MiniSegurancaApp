package com.example.minisegurancaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.minisegurancaapp.Security.RootDetector
import com.example.minisegurancaapp.Security.SecureStorage
import com.example.minisegurancaapp.Security.SslPinningClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val storage = remember { SecureStorage(applicationContext) }
            val sslClient = remember {
                SslPinningClient(
                    host = AppConstants.PINNED_HOST,
                    pinSha256 = AppConstants.PINNED_SHA256
                )
            }
            val scope = rememberCoroutineScope()
            var recoveredText by remember { mutableStateOf("Nenhum dado recuperado ainda.") }
            var sslStatus by remember { mutableStateOf("Nenhuma requisição SSL pinning executada ainda.") }
            val rooted = remember { RootDetector.isDeviceRooted() }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Nome: ${AppConstants.NOME}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "RA: ${AppConstants.RA}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Root detectado: ${if (rooted) "SIM" else "NÃO"}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(onClick = {
                            storage.save(AppConstants.NOME, AppConstants.RA)
                            recoveredText = "Dados salvos com EncryptedSharedPreferences."
                        }) {
                            Text("Salvar (EncryptedSharedPreferences)")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(onClick = {
                            val (nome, ra) = storage.load()
                            recoveredText =
                                if (nome == null && ra == null) {
                                    "Nada encontrado no armazenamento criptografado."
                                } else {
                                    "Recuperado: Nome=$nome | RA=$ra"
                                }
                        }) {
                            Text("Recuperar e mostrar")
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        Text(text = recoveredText)

                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = {
                            scope.launch {
                                sslStatus = "Executando requisição com SSL pinning..."
                                sslStatus = withContext(Dispatchers.IO) {
                                    try {
                                        val result = sslClient.makePinnedGet(AppConstants.PINNED_URL)
                                        "SSL pinning OK: $result"
                                    } catch (e: Exception) {
                                        "Falha SSL pinning/requisição: ${e.message ?: "erro desconhecido"}"
                                    }
                                }
                            }
                        }) {
                            Text("Requisição com SSL pinning")
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = sslStatus)
                    }
                }
            }
        }
    }
}

