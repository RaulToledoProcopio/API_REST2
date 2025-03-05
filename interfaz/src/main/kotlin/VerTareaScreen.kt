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
fun VerTareasScreen(token: String, onBack: () -> Unit) {
    val client = HttpClient {
        install(ContentNegotiation) { json() }
    }
    var tareasJson by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response: HttpResponse = client.get("https://api-rest2-xqzf.onrender.com/tareas/verTareas") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                tareasJson = if (response.status == HttpStatusCode.OK) {
                    response.bodyAsText()
                } else {
                    "Error al obtener tareas: ${response.status}"
                }
            } catch (e: Exception) {
                tareasJson = "Error de conexión: ${e.message}"
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
            Text(tareasJson, style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Volver a Tareas")
            }
        }
    }
}