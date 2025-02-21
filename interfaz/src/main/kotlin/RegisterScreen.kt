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
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordRepeat by remember { mutableStateOf("") }
    var calle by remember { mutableStateOf("") }
    var num by remember { mutableStateOf("") }
    var municipio by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var cp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Cliente HTTP Ktor
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
            Text("Registro", style = MaterialTheme.typography.h5)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = passwordRepeat, onValueChange = { passwordRepeat = it }, label = { Text("Repetir Password") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))

            Text("Dirección", style = MaterialTheme.typography.subtitle1)
            OutlinedTextField(value = calle, onValueChange = { calle = it }, label = { Text("Calle") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = num, onValueChange = { num = it }, label = { Text("Número") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = municipio, onValueChange = { municipio = it }, label = { Text("Municipio") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = provincia, onValueChange = { provincia = it }, label = { Text("Provincia") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = cp, onValueChange = { cp = it }, label = { Text("Código Postal") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                scope.launch {
                    try {
                        val response: HttpResponse = client.post("https://api-rest2-xqzf.onrender.com/usuarios/register") {
                            contentType(ContentType.Application.Json)
                            setBody(
                                """{
                            "username": "$username",
                            "email": "$email",
                            "password": "$password",
                            "passwordRepeat": "$passwordRepeat",
                            "rol": "USER",
                            "direccion": {
                                "provincia": "$provincia",
                                "municipio": "$municipio",
                                "calle": "$calle",
                                "num": "$num",
                                "cp": "$cp"
                            }
                        }"""
                            )
                        }

                        when (response.status) {
                            HttpStatusCode.OK -> {
                                errorMessage = null
                                onRegisterSuccess()
                            }
                            HttpStatusCode.BadRequest -> errorMessage = "Email ya registrado o datos inválidos"
                            HttpStatusCode.InternalServerError -> errorMessage = "Error interno del servidor"
                            else -> errorMessage = "Error desconocido: ${response.status}"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Error de conexión: ${e.message}"
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Registrarse")
            }

            errorMessage?.let { Text(it, color = MaterialTheme.colors.error) }
        }
    }
}