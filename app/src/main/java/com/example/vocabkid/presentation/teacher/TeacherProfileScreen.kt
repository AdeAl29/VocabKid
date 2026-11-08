package com.example.vocabkid.presentation.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.domain.model.StudentAvatar
import com.example.vocabkid.domain.model.UserRole
import com.example.vocabkid.presentation.components.KidTopBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun TeacherProfileScreen(
    repository: VocabKidRepository,
    firebaseAuth: FirebaseAuth,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val student by repository.observeStudent().collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(student?.id) {
        student?.let { name = it.name }
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = { KidTopBar(title = "Profil Guru", onBackClick = onBackClick) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(76.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.padding(20.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(name.ifBlank { "Guru VocaRepeat" }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Pengelola pembelajaran", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)) {
                        Text("GURU", modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Informasi akun", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; message = null },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nama guru") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )
                    OutlinedTextField(
                        value = firebaseAuth.currentUser?.email.orEmpty(),
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email guru") },
                        singleLine = true,
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                    )
                    OutlinedTextField(
                        value = "Guru",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Peran akun") },
                        singleLine = true,
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
                    )
                    message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                    Button(
                        onClick = {
                            val cleanName = name.trim()
                            if (cleanName.isBlank()) {
                                message = "Nama guru wajib diisi."
                            } else {
                                scope.launch {
                                    isSaving = true
                                    repository.saveStudent(
                                        name = cleanName,
                                        grade = student?.grade ?: 3,
                                        avatar = StudentAvatar.SISWA,
                                        nis = student?.nis.orEmpty(),
                                        role = UserRole.GURU
                                    )
                                    isSaving = false
                                    message = "Profil guru berhasil disimpan."
                                }
                            }
                        },
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Text(if (isSaving) "  Menyimpan..." else "  Simpan Profil")
                    }
                }
            }

            Spacer(Modifier.height(2.dp))
            OutlinedButton(onClick = onLogoutClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Text("  Keluar dari akun guru")
            }
        }
    }
}
