package com.saico.mimercado.feature.settings

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.mimercado.core.ui.util.AvatarUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showAvatarPicker by remember { mutableStateOf(false) }
    var familyNameInput by remember { mutableStateOf("") }

    if (showAvatarPicker) {
        AvatarPickerDialog(
            onDismiss = { showAvatarPicker = false },
            onAvatarSelected = { avatarId ->
                uiState.currentUserUid?.let { uid ->
                    viewModel.onAvatarChanged(uid, avatarId)
                }
                showAvatarPicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // USER PROFILE HEADER
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clickable { showAvatarPicker = true },
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = AvatarUtils.getAvatarEmoji(uiState.userAvatar),
                                        fontSize = 32.sp
                                    )
                                }
                            }

                            Spacer(Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "¡Hola, ${uiState.userDisplayName}!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (uiState.userEmail.isNotBlank()) {
                                    Text(
                                        text = uiState.userEmail,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // QUICK SETTINGS TILES
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(12.dp))
                                    Text("Tema Oscuro", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                }
                                Switch(
                                    checked = uiState.isDarkMode,
                                    onCheckedChange = { viewModel.toggleDarkMode() }
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.signOut() },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    Spacer(Modifier.width(12.dp))
                                    Text("Cerrar Sesión", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }

                // HOUSEHOLD MANAGEMENT SECTION
                val hasHousehold = uiState.household != null && uiState.household!!.id.isNotEmpty()

                if (!hasHousehold) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text("Sin Familia Asignada", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("Crea una familia para compartir y sincronizar listas con los tuyos.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)

                                Spacer(Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = familyNameInput,
                                    onValueChange = { familyNameInput = it },
                                    label = { Text("Nombre de la familia") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    placeholder = { Text("Ej: Los García") }
                                )

                                Spacer(Modifier.height(12.dp))

                                Button(
                                    onClick = { viewModel.createNewHousehold(familyNameInput) },
                                    enabled = familyNameInput.isNotBlank(),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.AddHome, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Crear mi Familia")
                                }
                            }
                        }
                    }
                } else {
                    uiState.household?.let { household ->
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = household.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text("Código de acceso familiar:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)

                                    Spacer(Modifier.height(12.dp))

                                    Text(
                                        text = household.joinCode,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 32.sp,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 4.sp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(Modifier.height(16.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        OutlinedButton(
                                            onClick = { clipboardManager.setText(AnnotatedString(household.joinCode)) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Copiar")
                                        }

                                        Button(
                                            onClick = {
                                                val sendIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, "¡Únete a mi familia en Mi Mercado! Código: ${household.joinCode}")
                                                    type = "text/plain"
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, null))
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Compartir")
                                        }
                                    }

                                    if (uiState.isCurrentUserAdult) {
                                        Spacer(Modifier.height(8.dp))
                                        TextButton(onClick = { viewModel.regenerateJoinCode() }, enabled = !uiState.isRegeneratingCode) {
                                            if (uiState.isRegeneratingCode) {
                                                CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                                            } else {
                                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(6.dp))
                                                Text("Regenerar código", fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // JOIN OTHER HOUSEHOLD CARD
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Unirse a otra familia", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Ingresa el código de 6 caracteres:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)

                            Spacer(Modifier.height(12.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = uiState.joinCodeInput,
                                    onValueChange = { if (it.length <= 6) viewModel.onJoinCodeInputChanged(it) },
                                    label = { Text("Código") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                Button(
                                    onClick = { viewModel.joinHouseholdWithCode() },
                                    enabled = uiState.joinCodeInput.trim().length == 6 && !uiState.isJoining,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(56.dp)
                                ) {
                                    if (uiState.isJoining) {
                                        CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.GroupAdd, contentDescription = null)
                                    }
                                }
                            }
                        }
                    }
                }

                // MEMBERS LIST
                if (hasHousehold) {
                    item {
                        Text(
                            "Integrantes de la familia",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(uiState.members) { member ->
                        MemberCardRow(
                            member = member,
                            isCurrentUser = member.uid == uiState.currentUserUid,
                            isEditable = uiState.isCurrentUserAdult,
                            onRoleChange = { role -> viewModel.onRoleChanged(member.uid, role) },
                            onAvatarClick = { if (member.uid == uiState.currentUserUid) showAvatarPicker = true }
                        )
                    }
                }

                uiState.error?.let { err ->
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                            Text(err, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp), fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
