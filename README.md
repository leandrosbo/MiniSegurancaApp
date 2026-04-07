## MiniSegurancaApp

Mini aplicação Android (1 tela) com:

- **Ofuscação** via R8/ProGuard (build `release`)
- **Detecção de root** (exibe status na tela)
- **Armazenamento criptografado** via `EncryptedSharedPreferences` (botões Salvar/Recuperar)
- **SSL pinning** com `CertificatePinner` + botão para requisição HTTPS

### Como abrir

Abra a pasta `MiniSegurancaApp` no Android Studio e aguarde a sincronização do Gradle.

### Onde colocar seu nome e RA

Edite os valores em `app/src/main/java/com/example/minisegurancaapp/AppConstants.kt`.

### Configuração de SSL pinning

No mesmo arquivo de constantes, configure:

- `PINNED_HOST`
- `PINNED_SHA256` (formato `sha256/BASE64...`)
- `PINNED_URL`

#### Passo a passo exato para gerar o PIN do seu domínio

1. Defina o domínio que a app vai acessar (sem `https://`), por exemplo: `api.seudominio.com`.
2. No **Windows PowerShell**, gere o pin usando `cmd /c` (evita corrupção de bytes no pipeline):

```powershell
cmd /c "openssl s_client -servername api.seudominio.com -connect api.seudominio.com:443 <NUL 2>NUL | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64"
```

3. O resultado será algo como `AbCdEf...=`.  
4. Monte o valor final adicionando o prefixo `sha256/`:

```text
sha256/AbCdEf...=
```

5. Cole no `app/src/main/java/com/example/minisegurancaapp/AppConstants.kt`.

#### Comando único (já retorna no formato final `sha256/...`)

```powershell
$pinBase64 = cmd /c "openssl s_client -servername api.seudominio.com -connect api.seudominio.com:443 <NUL 2>NUL | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64"

"sha256/$($pinBase64.Trim())"
```

#### Teste rápido (sucesso e falha)

1. Configure um host real, por exemplo `api.github.com`.
2. Gere o pin com o comando acima e cole em `PINNED_SHA256`.
3. Rode o app e toque no botão de requisição HTTPS:
   - com pin correto: retorna `HTTP 200 (OK)` (ou outro HTTP válido sem erro de pin);
   - com pin incorreto: retorna falha de pinning (`SSLPeerUnverifiedException` / `PIN FAILURE`).

#### Correção do problema no PowerShell (`sha256/Pz8...`)

Se aparecer algo como `sha256/Pz8...`, o pin foi gerado de forma corrompida por conversão de texto no pipeline do PowerShell.

- Não use pipeline direto com `openssl ... | openssl ...` no PowerShell para esse caso binário.
- Use sempre `cmd /c "..."` como mostrado nos comandos acima.
- Gere novamente e substitua `PINNED_SHA256`.

#### Valores prontos para copiar e colar

Use este bloco e troque apenas os placeholders:

```kotlin
const val PINNED_HOST = "api.seudominio.com"
const val PINNED_SHA256 = "sha256/COLE_AQUI_O_PIN_GERADO"
const val PINNED_URL = "https://api.seudominio.com/"
```

Exemplo didático (não use em produção, troque pelo seu):

```kotlin
const val PINNED_HOST = "example.com"
const val PINNED_SHA256 = "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
const val PINNED_URL = "https://example.com/"

    const val PINNED_HOST = "api.github.com"
    const val PINNED_SHA256 = "sha256/tt9RksdSBGiieTiyWkU8g3MOmCrfMcvXDGC4ZALs9rg="
    const val PINNED_URL  = "https://api.github.com/"
    
```

