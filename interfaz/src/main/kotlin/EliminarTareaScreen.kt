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
fun EliminarTareaScreen(token: String, onBack: () -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var assignedUsername by remember { mutableStateOf("") }
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
            Text("Eliminar Tarea", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = assignedUsername,
                onValueChange = { assignedUsername = it },
                label = { Text("Username asignado") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        try {
                            // Construir la URL manualmente con los parámetros
                            val url = "https://api-rest2-xqzf.onrender.com/tareas/eliminarTarea?titulo=$titulo&Username=$assignedUsername"
                            val response: HttpResponse = client.delete(url) {
                                headers { append(HttpHeaders.Authorization, "Bearer $token") }
                            }
                            if (response.status == HttpStatusCode.OK) {
                                errorMessage = "Tarea eliminada correctamente"
                                onBack()
                            } else {
                                errorMessage = "Error al eliminar tarea: ${response.status}"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error de conexión: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Eliminar Tarea")
            }
            errorMessage?.let { Text(it, color = MaterialTheme.colors.error) }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Volver a Tareas")
            }
        }
    }
}