import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TareasScreen(token: String, username: String, onLogout: () -> Unit) {
    var showCreateTarea by remember { mutableStateOf(false) }
    var showDeleteTarea by remember { mutableStateOf(false) }
    var showUpdateTarea by remember { mutableStateOf(false) }
    var showViewTareas by remember { mutableStateOf(false) }

    when {
        showCreateTarea -> CrearTareaScreen(token = token, onBack = { showCreateTarea = false })
        showDeleteTarea -> EliminarTareaScreen(token = token, onBack = { showDeleteTarea = false })
        showUpdateTarea -> ActualizarTareaScreen(token = token, onBack = { showUpdateTarea = false })
        showViewTareas -> VerTareasScreen(token = token, onBack = { showViewTareas = false })
        else -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Pantalla de Tareas", style = MaterialTheme.typography.h5)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showCreateTarea = true },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Crear Tarea") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showDeleteTarea = true },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Eliminar Tarea") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showUpdateTarea = true },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Actualizar Tarea") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showViewTareas = true },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Ver Tareas") }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Cerrar sesión") }
            }
        }
    }
}