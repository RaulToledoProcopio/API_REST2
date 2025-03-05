import androidx.compose.runtime.*
import androidx.compose.ui.window.singleWindowApplication

fun main() = singleWindowApplication {
    var screen by remember { mutableStateOf("login") }
    var token by remember { mutableStateOf<String?>(null) }
    var username by remember { mutableStateOf("") }

    when (screen) {
        "login" -> LoginScreen(
            onLoginSuccess = { receivedToken, user ->
                token = receivedToken
                username = user
                screen = "tareas"
            },
            onRegisterClick = { screen = "register" }
        )
        "register" -> RegisterScreen(
            onRegisterSuccess = { screen = "login" }
        )
        "tareas" -> TareasScreen(
            token = token ?: "",
            username = username,
            onLogout = {
                token = null
                username = ""
                screen = "login"
            }
        )
    }
}
