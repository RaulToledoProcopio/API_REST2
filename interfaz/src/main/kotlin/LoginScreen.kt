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

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Añade margen a la pantalla
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center, // Centra los elementos
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Login", style = MaterialTheme.typography.h5)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())

            Button(onClick = {
                scope.launch {
                    try {
                        val response: HttpResponse = client.post("https://api-rest2-xqzf.onrender.com/usuarios/login") {
                            contentType(ContentType.Application.Json)
                            setBody(
                                """{
                                        "username": "$username",
                                        "password": "$password"
                                    }"""
                            )
                        }
                        if (response.status == HttpStatusCode.Created) {
                            errorMessage = "Credenciales correctas"
                            onLoginSuccess()
                        } else {
                            errorMessage = "Credenciales incorrectas"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.message}"
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Iniciar Sesión")
            }

            errorMessage?.let { Text(it, color = MaterialTheme.colors.error) }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para ir a la pantalla de registro
            TextButton(onClick = onRegisterClick) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}