package com.example.vocabkid.presentation.auth

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onLoginSubmit: (String, String) -> Unit,
    onRegisterSubmit: (String, String, String, String, Int) -> Unit,
    onResetPasswordSubmit: (String) -> Unit
) {
    var mode by rememberSaveable { mutableStateOf(AuthMode.Login) }
    var loginIdentity by rememberSaveable { mutableStateOf("") }
    var registerName by rememberSaveable { mutableStateOf("") }
    var registerNis by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var grade by rememberSaveable { mutableIntStateOf(3) }
    var isResetDialogOpen by rememberSaveable { mutableStateOf(false) }
    var resetEmail by rememberSaveable { mutableStateOf("") }
    var hasResetAttempted by rememberSaveable { mutableStateOf(false) }

    if (isResetDialogOpen) {
        ResetPasswordDialog(
            email = resetEmail,
            onEmailChange = { resetEmail = it },
            isLoading = isLoading,
            hasAttempted = hasResetAttempted,
            errorMessage = errorMessage,
            successMessage = successMessage,
            onSubmit = {
                hasResetAttempted = true
                onResetPasswordSubmit(resetEmail)
            },
            onDismiss = {
                if (!isLoading) {
                    isResetDialogOpen = false
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            AuthLearningIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(208.dp)
            )

            AuthPanel(
                mode = mode,
                onModeChange = { mode = it },
                loginIdentity = loginIdentity,
                onLoginIdentityChange = { loginIdentity = it },
                registerName = registerName,
                onRegisterNameChange = { registerName = it },
                registerNis = registerNis,
                onRegisterNisChange = { registerNis = it },
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                grade = grade,
                onGradeChange = { grade = it },
                isLoading = isLoading,
                errorMessage = errorMessage,
                successMessage = successMessage,
                onForgotPasswordClick = {
                    resetEmail = loginIdentity
                    hasResetAttempted = false
                    isResetDialogOpen = true
                },
                onSubmit = {
                    when (mode) {
                        AuthMode.Login -> onLoginSubmit(loginIdentity, password)
                        AuthMode.Register -> onRegisterSubmit(registerName, registerNis, email, password, grade)
                    }
                }
            )
        }
    }
}

@Composable
private fun AuthPanel(
    mode: AuthMode,
    onModeChange: (AuthMode) -> Unit,
    loginIdentity: String,
    onLoginIdentityChange: (String) -> Unit,
    registerName: String,
    onRegisterNameChange: (String) -> Unit,
    registerNis: String,
    onRegisterNisChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    grade: Int,
    onGradeChange: (Int) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onForgotPasswordClick: () -> Unit,
    onSubmit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AuthModeSelector(
                selectedMode = mode,
                onModeChange = onModeChange
            )

            Crossfade(
                targetState = mode,
                animationSpec = tween(durationMillis = 220),
                label = "authFormMode"
            ) { targetMode ->
                when (targetMode) {
                    AuthMode.Login -> LoginFields(
                        identity = loginIdentity,
                        onIdentityChange = onLoginIdentityChange,
                        password = password,
                        onPasswordChange = onPasswordChange,
                        onForgotPasswordClick = onForgotPasswordClick
                    )

                    AuthMode.Register -> RegisterFields(
                        name = registerName,
                        onNameChange = onRegisterNameChange,
                        nis = registerNis,
                        onNisChange = onRegisterNisChange,
                        email = email,
                        onEmailChange = onEmailChange,
                        password = password,
                        onPasswordChange = onPasswordChange,
                        grade = grade,
                        onGradeChange = onGradeChange
                    )
                }
            }

            errorMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }

            successMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onSubmit,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = if (mode == AuthMode.Login) {
                        Icons.AutoMirrored.Filled.Login
                    } else {
                        Icons.Default.PersonAdd
                    },
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        isLoading -> "Menyiapkan..."
                        mode == AuthMode.Login -> "Masuk Sekarang"
                        else -> "Buat Akun"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = if (mode == AuthMode.Login) {
                    "Belum punya akun? Daftar dulu"
                } else {
                    "Sudah punya akun? Masuk"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onModeChange(
                            if (mode == AuthMode.Login) {
                                AuthMode.Register
                            } else {
                                AuthMode.Login
                            }
                        )
                    }
                    .padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun AuthModeSelector(
    selectedMode: AuthMode,
    onModeChange: (AuthMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AuthModeTab(
            mode = AuthMode.Login,
            selected = selectedMode == AuthMode.Login,
            icon = Icons.AutoMirrored.Filled.Login,
            onClick = { onModeChange(AuthMode.Login) },
            modifier = Modifier.weight(1f)
        )
        AuthModeTab(
            mode = AuthMode.Register,
            selected = selectedMode == AuthMode.Register,
            icon = Icons.Default.PersonAdd,
            onClick = { onModeChange(AuthMode.Register) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AuthModeTab(
    mode: AuthMode,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 180),
        label = "authTabContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 180),
        label = "authTabContent"
    )

    Row(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = mode.label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LoginFields(
    identity: String,
    onIdentityChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Masuk akun",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Lanjutkan latihan, ulangi kata penting, dan lihat progres belajarmu.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = identity,
            onValueChange = onIdentityChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            }
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            }
        )
        Text(
            text = "Lupa password?",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onForgotPasswordClick)
                .padding(top = 2.dp, bottom = 2.dp)
        )
    }
}

@Composable
private fun ResetPasswordDialog(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    hasAttempted: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val hasSuccess = hasAttempted && successMessage != null

    AlertDialog(
        onDismissRequest = {
            if (!isLoading) {
                onDismiss()
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Reset password",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Masukkan email akun. Firebase akan mengirim link konfirmasi untuk membuat password baru.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email akun") },
                    enabled = !isLoading && !hasSuccess,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    }
                )
                if (hasAttempted && errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (hasSuccess) {
                    Text(
                        text = successMessage.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (hasSuccess) {
                        onDismiss()
                    } else {
                        onSubmit()
                    }
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = when {
                        hasSuccess -> "Selesai"
                        isLoading -> "Mengirim..."
                        else -> "Kirim Email"
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            if (!hasSuccess) {
                TextButton(
                    onClick = onDismiss,
                    enabled = !isLoading
                ) {
                    Text("Batal")
                }
            }
        },
        shape = RoundedCornerShape(8.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun RegisterFields(
    name: String,
    onNameChange: (String) -> Unit,
    nis: String,
    onNisChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    grade: Int,
    onGradeChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Daftar siswa",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nama siswa") },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            }
        )
        OutlinedTextField(
            value = nis,
            onValueChange = { value -> onNisChange(value.filter { it.isDigit() }) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("NIS (Nomor Induk Siswa)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Badge,
                    contentDescription = null
                )
            }
        )
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email orang tua") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            }
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            }
        )
        GradePicker(
            selectedGrade = grade,
            onGradeSelected = onGradeChange
        )
    }
}

@Composable
private fun GradePicker(
    selectedGrade: Int,
    onGradeSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Kelas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(3, 4).forEach { grade ->
                FilterChip(
                    selected = selectedGrade == grade,
                    onClick = { onGradeSelected(grade) },
                    label = { Text("Kelas $grade") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(5, 6).forEach { grade ->
                FilterChip(
                    selected = selectedGrade == grade,
                    onClick = { onGradeSelected(grade) },
                    label = { Text("Kelas $grade") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AuthLearningIllustration(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "authLearningIllustration")
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "authIllustrationBob"
    )
    val sway by transition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "authIllustrationSway"
    )
    val sparkle by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "authIllustrationSparkle"
    )

    val primary = MaterialTheme.colorScheme.primary
    val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val secondary = MaterialTheme.colorScheme.secondary
    val secondaryContainer = MaterialTheme.colorScheme.secondaryContainer
    val onSecondaryContainer = MaterialTheme.colorScheme.onSecondaryContainer
    val tertiary = MaterialTheme.colorScheme.tertiary
    val tertiaryContainer = MaterialTheme.colorScheme.tertiaryContainer
    val onTertiaryContainer = MaterialTheme.colorScheme.onTertiaryContainer
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawAuthScene(
                bob = bob,
                sparkle = sparkle,
                primary = primary,
                primaryContainer = primaryContainer,
                secondary = secondary,
                secondaryContainer = secondaryContainer,
                tertiary = tertiary,
                tertiaryContainer = tertiaryContainer,
                surface = surface,
                outline = outline
            )
        }

        FloatingStudyChip(
            text = "READ",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            containerColor = tertiaryContainer,
            contentColor = onTertiaryContainer,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 10.dp, top = 22.dp)
                .graphicsLayer {
                    translationY = bob
                    rotationZ = -4f + sway
                }
        )
        FloatingStudyChip(
            text = "SAY",
            icon = Icons.AutoMirrored.Filled.VolumeUp,
            containerColor = secondaryContainer,
            contentColor = onSecondaryContainer,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 54.dp)
                .graphicsLayer {
                    translationY = -bob
                    rotationZ = 3f - sway
                }
        )
        FloatingStudyChip(
            text = "A+",
            icon = Icons.Default.AutoAwesome,
            containerColor = primaryContainer,
            contentColor = onPrimaryContainer,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 34.dp, bottom = 28.dp)
                .graphicsLayer {
                    translationY = bob * 0.62f
                    rotationZ = 2f + sway
                }
        )
    }
}

private fun DrawScope.drawAuthScene(
    bob: Float,
    sparkle: Float,
    primary: Color,
    primaryContainer: Color,
    secondary: Color,
    secondaryContainer: Color,
    tertiary: Color,
    tertiaryContainer: Color,
    surface: Color,
    outline: Color
) {
    val w = size.width
    val h = size.height
    val radius = 8.dp.toPx()
    val centerOffset = bob * density

    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                surface.copy(alpha = 0.84f),
                primaryContainer.copy(alpha = 0.74f),
                tertiaryContainer.copy(alpha = 0.68f)
            )
        ),
        topLeft = Offset(w * 0.14f, h * 0.17f),
        size = Size(w * 0.72f, h * 0.68f),
        cornerRadius = CornerRadius(radius, radius)
    )

    drawRoundRect(
        color = secondaryContainer.copy(alpha = 0.95f),
        topLeft = Offset(w * 0.28f, h * 0.36f + centerOffset),
        size = Size(w * 0.44f, h * 0.32f),
        cornerRadius = CornerRadius(radius, radius)
    )
    drawRoundRect(
        color = surface,
        topLeft = Offset(w * 0.31f, h * 0.32f + centerOffset),
        size = Size(w * 0.18f, h * 0.32f),
        cornerRadius = CornerRadius(radius, radius)
    )
    drawRoundRect(
        color = surface,
        topLeft = Offset(w * 0.51f, h * 0.32f + centerOffset),
        size = Size(w * 0.18f, h * 0.32f),
        cornerRadius = CornerRadius(radius, radius)
    )
    drawRoundRect(
        color = primary.copy(alpha = 0.85f),
        topLeft = Offset(w * 0.492f, h * 0.34f + centerOffset),
        size = Size(w * 0.016f, h * 0.27f),
        cornerRadius = CornerRadius(radius, radius)
    )

    val lineColor = outline.copy(alpha = 0.42f)
    repeat(3) { index ->
        val y = h * (0.39f + index * 0.065f) + centerOffset
        drawRoundRect(
            color = lineColor,
            topLeft = Offset(w * 0.34f, y),
            size = Size(w * 0.105f, 3.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
        drawRoundRect(
            color = lineColor,
            topLeft = Offset(w * 0.55f, y),
            size = Size(w * 0.095f, 3.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
    }

    drawRoundRect(
        color = tertiary.copy(alpha = 0.92f),
        topLeft = Offset(w * 0.58f, h * 0.24f - centerOffset * 0.6f),
        size = Size(w * 0.125f, h * 0.09f),
        cornerRadius = CornerRadius(radius, radius)
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.705f, h * 0.285f - centerOffset * 0.6f)
            lineTo(w * 0.755f, h * 0.255f - centerOffset * 0.6f)
            lineTo(w * 0.755f, h * 0.315f - centerOffset * 0.6f)
            close()
        },
        color = tertiary.copy(alpha = 0.92f)
    )

    drawSparkle(
        center = Offset(w * 0.24f, h * 0.24f),
        radius = 12.dp.toPx() * sparkle,
        color = secondary.copy(alpha = 0.86f)
    )
    drawSparkle(
        center = Offset(w * 0.78f, h * 0.72f),
        radius = 10.dp.toPx() * (1.18f - sparkle * 0.18f),
        color = primary.copy(alpha = 0.72f)
    )
}

private fun DrawScope.drawSparkle(
    center: Offset,
    radius: Float,
    color: Color
) {
    drawLine(
        color = color,
        start = Offset(center.x - radius, center.y),
        end = Offset(center.x + radius, center.y),
        strokeWidth = 3.dp.toPx()
    )
    drawLine(
        color = color,
        start = Offset(center.x, center.y - radius),
        end = Offset(center.x, center.y + radius),
        strokeWidth = 3.dp.toPx()
    )
}

@Composable
private fun FloatingStudyChip(
    text: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = containerColor.copy(alpha = 0.96f),
        contentColor = contentColor,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.16f)),
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
        }
    }
}

private enum class AuthMode(
    val label: String
) {
    Login("Login"),
    Register("Register")
}
