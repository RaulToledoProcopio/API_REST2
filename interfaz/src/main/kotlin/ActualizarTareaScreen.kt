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
fun ActualizarTareaScreen(token: String?, onBack: () -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var completada by remember { mutableStateOf(false) }
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
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Actualizar Tarea", style = MaterialTheme.typography.h5)

            Spacer(modifier = Modifier.height(8.dp))

            // Campo para ingresar el título de la tarea
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la tarea") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para actualizar la tarea
            Button(onClick = {
                scope.launch {
                    try {
                        val response: HttpResponse = client.put("https://api-rest2-xqzf.onrender.com/tareas/actualizarTarea?titulo=$titulo") {
                            contentType(ContentType.Application.Json)
                            headers {
                                append(HttpHeaders.Authorization, "Bearer $token") // Aquí agregas el token en la cabecera
                            }
                            setBody(
                                """{
                                    "completada": $completada
                                }"""
                            )
                        }

                        // Verifica el código de respuesta
                        if (response.status == HttpStatusCode.OK) {
                            errorMessage = "Tarea actualizada correctamente"
                        } else {
                            errorMessage = "Error al actualizar tarea: ${response.status}"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Error de conexión: ${e.message}"
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Actualizar Tarea")
            }

            // Mostrar el mensaje de error si lo hay
            errorMessage?.let { Text(it, color = MaterialTheme.colors.error) }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para volver a la pantalla de tareas
            Button(onClick = onBack) {
                Text("Volver a Tareas")
            }
        }
    }
}