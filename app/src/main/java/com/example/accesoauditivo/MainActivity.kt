package com.example.accesoauditivo

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.accesoauditivo.ui.theme.AccesoAuditivoTheme
import java.util.Locale

data class AppUser(
    val name: String,
    val email: String,
    val password: String,
    val supportNeed: String
)

data class SupportTool(
    val name: String,
    val description: String
)

enum class Screen {
    Login,
    Register,
    Recover,
    Home
}

private val PrimaryTeal = Color(0xFF0B6E69)
private val PrimaryTealDark = Color(0xFF064B47)
private val SoftTeal = Color(0xFFE4F2EF)
private val SoftBlue = Color(0xFFEAF0FF)
private val SoftAmber = Color(0xFFFFF4DF)
private val AppCanvas = Color(0xFFF4F7F9)
private val MutedText = Color(0xFF506070)
private val ErrorRed = Color(0xFFB3261E)
private val SuccessGreen = Color(0xFF176B4D)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AccesoAuditivoTheme {
                AccesoAuditivoApp()
            }
        }
    }
}

@Composable
fun AccesoAuditivoApp() {
    val users = remember {
        mutableStateListOf(
            AppUser("Ana Silva", "ana@demo.cl", "1234", "Lectura labial"),
            AppUser("Bruno Mora", "bruno@demo.cl", "abcd", "Texto a voz"),
            AppUser("Camila Soto", "camila@demo.cl", "pass1", "Alertas visuales"),
            AppUser("Diego Rojas", "diego@demo.cl", "demo2", "Subtitulos"),
            AppUser("Elena Perez", "elena@demo.cl", "clave5", "Vibracion")
        )
    }
    var screen by remember { mutableStateOf(Screen.Login) }
    var activeUser by remember { mutableStateOf<AppUser?>(null) }
    var message by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = AppCanvas
        ) {
            when (screen) {
                Screen.Login -> LoginScreen(
                    users = users,
                    message = message,
                    onLogin = { email, password ->
                        val found = users.firstOrNull {
                            it.email.equals(email.trim(), ignoreCase = true) && it.password == password
                        }
                        if (found != null) {
                            activeUser = found
                            message = ""
                            screen = Screen.Home
                        } else {
                            message = "Correo o contrasena incorrectos."
                        }
                    },
                    onRegister = {
                        message = ""
                        screen = Screen.Register
                    },
                    onRecover = {
                        message = ""
                        screen = Screen.Recover
                    }
                )

                Screen.Register -> RegisterScreen(
                    onBack = {
                        message = ""
                        screen = Screen.Login
                    },
                    onCreateUser = { user ->
                        users.add(user)
                        message = "Usuario registrado. Ahora puedes iniciar sesion."
                        screen = Screen.Login
                    }
                )

                Screen.Recover -> RecoverScreen(
                    users = users,
                    onBack = {
                        message = ""
                        screen = Screen.Login
                    }
                )

                Screen.Home -> HomeScreen(
                    activeUser = activeUser,
                    users = users,
                    onLogout = {
                        activeUser = null
                        message = ""
                        screen = Screen.Login
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    users: List<AppUser>,
    message: String,
    onLogin: (String, String) -> Unit,
    onRegister: () -> Unit,
    onRecover: () -> Unit
) {
    var email by remember { mutableStateOf("ana@demo.cl") }
    var password by remember { mutableStateOf("1234") }
    var rememberUser by remember { mutableStateOf(true) }
    var accessMode by remember { mutableStateOf("Texto") }

    AppFrame(title = "AccesoAuditivo", subtitle = "Comunicacion inclusiva para personas con discapacidad auditiva") {
        CardPanel {
            Text("Inicio de sesion", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Usuario de prueba: ana@demo.cl / 1234", color = MutedText)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electronico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contrasena") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = rememberUser, onCheckedChange = { rememberUser = it })
                Text("Recordar mis datos")
            }

            Text("Modo de comunicacion preferido", fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = accessMode == "Texto", onClick = { accessMode = "Texto" })
                Text("Texto")
                Spacer(Modifier.width(12.dp))
                RadioButton(selected = accessMode == "Visual", onClick = { accessMode = "Visual" })
                Text("Visual")
            }

            if (message.isNotBlank()) {
                MessageBanner(text = message, isError = true)
            }

            Button(
                onClick = { onLogin(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onRegister) { Text("Registrarme") }
                TextButton(onClick = onRecover) { Text("Recuperar contrasena") }
            }
        }

        Spacer(Modifier.height(14.dp))
        Text("Usuarios precargados en el arreglo", fontWeight = FontWeight.Bold)
        CredentialTable(users = users.take(5))
    }
}

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onCreateUser: (AppUser) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }
    var supportNeed by remember { mutableStateOf("Texto a voz") }
    var menuOpen by remember { mutableStateOf(false) }
    val options = listOf("Texto a voz", "Alertas visuales", "Subtitulos", "Vibracion")
    val canSubmit = name.isNotBlank() && email.isNotBlank() && password.length >= 4 && acceptTerms

    AppFrame(title = "Registro", subtitle = "Crea un perfil con preferencias de accesibilidad") {
        CardPanel {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electronico") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contrasena minimo 4 caracteres") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            Text("Necesidad principal", fontWeight = FontWeight.SemiBold)
            Box {
                OutlinedButton(onClick = { menuOpen = true }) {
                    Text(supportNeed)
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                supportNeed = option
                                menuOpen = false
                            }
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = acceptTerms, onCheckedChange = { acceptTerms = it })
                Text("Acepto el uso de mis datos para crear la cuenta")
            }

            Button(
                onClick = {
                    onCreateUser(
                        AppUser(
                            name = name.trim(),
                            email = email.trim(),
                            password = password,
                            supportNeed = supportNeed
                        )
                    )
                },
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear cuenta")
            }
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al login")
            }
        }
    }
}

@Composable
fun RecoverScreen(
    users: List<AppUser>,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    AppFrame(title = "Recuperar contrasena", subtitle = "Busca tu cuenta registrada") {
        CardPanel {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electronico") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    val found = users.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
                    result = if (found != null) {
                        "Cuenta encontrada. Pista temporal: ${found.password}"
                    } else {
                        "No existe una cuenta asociada a ese correo."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar cuenta")
            }
            if (result.isNotBlank()) {
                MessageBanner(text = result, isError = result.startsWith("No"))
            }
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}

@Composable
fun HomeScreen(
    activeUser: AppUser?,
    users: List<AppUser>,
    onLogout: () -> Unit
) {
    val tools = listOf(
        SupportTool("Texto rapido", "Frases listas para mostrar"),
        SupportTool("Voz a texto", "Apoyo para conversar"),
        SupportTool("Alertas visuales", "Avisos destacados"),
        SupportTool("Subtitulos", "Lectura de contenido"),
        SupportTool("Vibracion", "Senales tactiles"),
        SupportTool("Contactos", "Red de apoyo")
    )

    AppFrame(
        title = "Panel principal",
        subtitle = "Bienvenido/a ${activeUser?.name ?: "usuario"}"
    ) {
        CardPanel {
            Text("Herramientas de apoyo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(220.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tools) { tool ->
                    ToolTile(tool = tool)
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        QuickCommunicationPanel()
        Spacer(Modifier.height(14.dp))
        Text("Resumen de usuarios", fontWeight = FontWeight.Bold)
        SupportTable(users = users)
        Spacer(Modifier.height(14.dp))
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesion")
        }
    }
}

@Composable
fun QuickCommunicationPanel() {
    val context = LocalContext.current
    var message by remember { mutableStateOf("Necesito ayuda para comunicarme") }
    var outputMode by remember { mutableStateOf("Mostrar texto") }
    var isUrgent by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("Necesidad") }
    var menuOpen by remember { mutableStateOf(false) }
    var generatedMessage by remember { mutableStateOf("") }
    var ttsReady by remember { mutableStateOf(false) }
    val textToSpeech = remember {
        TextToSpeech(context) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
        }
    }
    val categories = listOf("Necesidad", "Saludo", "Emergencia", "Transporte")
    val phrases = listOf(
        "Por favor escribe tu respuesta",
        "No escucho bien",
        "Necesito indicaciones",
        "Avisar a contacto",
        "Tengo una emergencia",
        "Gracias por ayudar"
    )

    LaunchedEffect(ttsReady) {
        if (ttsReady) {
            textToSpeech.language = Locale.forLanguageTag("es-CL")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    CardPanel {
        Text("Comunicador rapido", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Herramienta para escribir o preparar un mensaje hablado/visual.", color = MutedText)
        MessageBanner(
            text = if (ttsReady) "Voz disponible en este dispositivo." else "Preparando motor de voz...",
            isError = false
        )

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Mensaje para comunicar") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Categoria del mensaje", fontWeight = FontWeight.SemiBold)
        Box {
            OutlinedButton(onClick = { menuOpen = true }) {
                Text(category)
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                categories.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            category = option
                            menuOpen = false
                        }
                    )
                }
            }
        }

        Text("Salida preferida", fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = outputMode == "Mostrar texto", onClick = { outputMode = "Mostrar texto" })
            Text("Texto")
            Spacer(Modifier.width(12.dp))
            RadioButton(selected = outputMode == "Preparar voz", onClick = { outputMode = "Preparar voz" })
            Text("Hablar")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isUrgent, onCheckedChange = { isUrgent = it })
            Text("Marcar como alerta visual")
        }

        Text("Frases rapidas", fontWeight = FontWeight.SemiBold)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(190.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(phrases) { phrase ->
                PhraseTile(text = phrase, onClick = { message = phrase })
            }
        }

        Button(
            onClick = {
                val prefix = if (isUrgent) "ALERTA VISUAL: " else ""
                generatedMessage = "$prefix$category - $outputMode: $message"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generar mensaje")
        }
        Button(
            onClick = {
                val text = message.trim()
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "mensaje_accesible")
                generatedMessage = "Reproduciendo mensaje: $text"
            },
            enabled = ttsReady && message.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reproducir mensaje")
        }
        OutlinedButton(
            onClick = {
                message = ""
                generatedMessage = ""
                isUrgent = false
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Limpiar")
        }

        if (generatedMessage.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isUrgent) Color(0xFFFFE1DD) else SoftTeal, RoundedCornerShape(8.dp))
                    .border(
                        1.dp,
                        if (isUrgent) ErrorRed else PrimaryTeal,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(14.dp)
            ) {
                Text(generatedMessage, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { generatedMessage = "Vinculo informativo: Guia de accesibilidad auditiva" }) {
                Text("Guia")
            }
            TextButton(onClick = { generatedMessage = "Vinculo de ayuda: Contacto de soporte" }) {
                Text("Soporte")
            }
        }
    }
}

@Composable
fun MessageBanner(text: String, isError: Boolean) {
    val background = if (isError) Color(0xFFFFE1DD) else SoftBlue
    val border = if (isError) ErrorRed else Color(0xFF6C7FB7)
    val foreground = if (isError) ErrorRed else Color(0xFF2D426E)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(8.dp))
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(text, color = foreground, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun StatusPill(text: String) {
    Box(
        modifier = Modifier
            .background(SoftAmber, RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE6C170), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text, color = Color(0xFF5A4300), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AppFrame(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF0B6E69), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("AA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, color = MutedText)
                Spacer(Modifier.height(6.dp))
                StatusPill("Prototipo Android - Jetpack Compose")
            }
        }
        Spacer(Modifier.height(22.dp))
        content()
    }
}

@Composable
fun CardPanel(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            content = content
        )
    }
}

@Composable
fun ToolTile(tool: SupportTool) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = SoftTeal),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(86.dp)
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(tool.name, fontWeight = FontWeight.Bold, color = PrimaryTealDark)
            Text(tool.description, color = MutedText, fontSize = 12.sp)
        }
    }
}

@Composable
fun PhraseTile(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text)
    }
}

@Composable
fun CredentialTable(users: List<AppUser>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFD6DEE8), RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        ThreeColumnTableRow("Nombre", "Correo", "Clave", isHeader = true)
        users.forEach { user ->
            ThreeColumnTableRow(user.name, user.email, user.password)
        }
    }
}

@Composable
fun SupportTable(users: List<AppUser>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFD6DEE8), RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        ThreeColumnTableRow("Nombre", "Correo", "Apoyo", isHeader = true)
        users.forEach { user ->
            ThreeColumnTableRow(user.name, user.email, user.supportNeed)
        }
    }
}

@Composable
fun ThreeColumnTableRow(first: String, second: String, third: String, isHeader: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isHeader) PrimaryTeal else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = first,
            modifier = Modifier.weight(0.8f),
            color = if (isHeader) Color.White else Color(0xFF1C1F24),
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = second,
            modifier = Modifier.weight(1.1f),
            color = if (isHeader) Color.White else Color(0xFF1C1F24),
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = third,
            modifier = Modifier.weight(0.8f),
            color = if (isHeader) Color.White else Color(0xFF1C1F24),
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccesoAuditivoPreview() {
    AccesoAuditivoTheme {
        AccesoAuditivoApp()
    }
}
