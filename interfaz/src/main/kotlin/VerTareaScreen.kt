import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun VerTareasScreen(token: String, onBack: () -> Unit) {
    val client = HttpClient {
        install(ContentNegotiation) { json() }
    }

    var tareasList by remember { mutableStateOf<List<String>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response: HttpResponse = client.get("https://api-rest2-xqzf.onrender.com/tareas/verTareas") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }

                if (response.status == HttpStatusCode.OK) {
                    // Parsear el JSON
                    val jsonResponse = response.bodyAsText()
                    val jsonArray = Json.parseToJsonElement(jsonResponse).jsonArray

                    // Mapear el JSON a una lista de cadenas
                    tareasList = jsonArray.map { tarea ->
                        val jsonObject = tarea as JsonObject
                        "Título: ${jsonObject["titulo"]?.jsonPrimitive?.content ?: "No disponible"}\nUsuario: ${jsonObject["username"]?.jsonPrimitive?.content ?: "No disponible"}"
                    }
                } else {
                    errorMessage = "Error al obtener tareas: ${response.status}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
            }
        }
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
            Text("Ver Tareas", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(8.dp))

            // Mostrar mensaje de error si ocurre alguno
            errorMessage?.let {
                Text(it, color = MaterialTheme.colors.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Si no hay tareas, mostramos un mensaje
            if (tareasList.isEmpty()) {
                Text("No hay tareas disponibles.")
            } else {
                // Mostrar las tareas en un LazyColumn
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tareasList) { tarea ->
                        // Mostrar cada tarea en una Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(tarea, style = MaterialTheme.typography.body1)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Volver a Tareas")
            }
        }
    }
}