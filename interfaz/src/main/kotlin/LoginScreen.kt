import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun LoginScreen(onLoginSuccess: (String, String) -> Unit, onRegisterClick: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val client = HttpClient {
        install(ContentNegotiation) { json() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Login", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val response: HttpResponse = client.post("https://api-rest2-xqzf.onrender.com/usuarios/login") {
                                contentType(ContentType.Application.Json)
                                setBody("""{ "username": "$username", "password": "$password" }""")
                            }
                            when (response.status) {
                                HttpStatusCode.Created -> {
                                    val jsonResponse = Json.decodeFromString<Map<String, String>>(response.bodyAsText())
                                    val token = jsonResponse["token"]
                                    if (token != null) {
                                        onLoginSuccess(token, username)
                                    } else {
                                        errorMessage = "Token no recibido en la respuesta."
                                    }
                                }
                                HttpStatusCode.NotFound -> errorMessage = "Usuario no encontrado"
                                HttpStatusCode.Unauthorized -> errorMessage = "Credenciales incorrectas"
                                HttpStatusCode.BadRequest -> errorMessage = "Error en la validación de datos"
                                HttpStatusCode.InternalServerError -> errorMessage = "Error interno del servidor"
                                else -> errorMessage = "Error desconocido: ${response.status}"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error de conexión: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }
            errorMessage?.let {
                Text(it, color = MaterialTheme.colors.error)
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onRegisterClick) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}