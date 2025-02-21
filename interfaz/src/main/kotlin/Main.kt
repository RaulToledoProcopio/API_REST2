import androidx.compose.runtime.*
import androidx.compose.ui.window.singleWindowApplication

fun main() = singleWindowApplication {
    var showRegister by remember { mutableStateOf(false) }

    if (showRegister) {
        RegisterScreen { showRegister = false } // Volver a Login después de registrarse
    } else {
        LoginScreen(onLoginSuccess = { println("Usuario logueado con éxito!") }, onRegisterClick = { showRegister = true })
    }
}