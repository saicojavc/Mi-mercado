package com.saico.mimercado.feature.settings

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.saico.mimercado.core.model.HouseholdMember
import com.saico.mimercado.core.model.MemberRole
import com.saico.mimercado.core.ui.util.AvatarUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdSettingsScreen(
    viewModel: HouseholdSettingsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var familyNameInput by remember { mutableStateOf("") }
    var showAvatarPicker by remember { mutableStateOf(false) }

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
                title = { Text("Ajustes del Hogar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                val hasHousehold = uiState.household != null && uiState.household!!.id.isNotEmpty()

                // CASO: No tiene hogar o fue borrado -> Mostrar panel de creación
                if (!hasHousehold) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text("No tienes una familia asignada", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("Crea una nueva familia para compartir listas de compras y sincronizar carritos con otros miembros.", color = Color.Gray, fontSize = 14.sp)
                                
                                Spacer(Modifier.height(20.dp))
                                
                                OutlinedTextField(
                                    value = familyNameInput,
                                    onValueChange = { familyNameInput = it },
                                    label = { Text("Nombre de tu familia") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    placeholder = { Text("Ej: Los García") }
                                )
                                
                                Spacer(Modifier.height(16.dp))
                                
                                Button(
                                    onClick = { viewModel.createNewHousehold(familyNameInput) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = familyNameInput.isNotBlank(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.AddHome, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Crear mi Familia")
                                }
                            }
                        }
                    }
                }

                // PANEL PRINCIPAL: Código de acceso (Solo si ya existe un hogar)
                if (hasHousehold) {
                    uiState.household?.let { household ->
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                            ) {
                                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = household.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text("Código único de acceso familiar:", color = Color.Gray, fontSize = 14.sp)
                                    
                                    Spacer(Modifier.height(12.dp))
                                    
                                    Text(
                                        text = household.joinCode,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 36.sp,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 4.sp,
                                        color = MaterialTheme.colorScheme.primary,
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
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Copiar")
                                        }

                                        Button(
                                            onClick = {
                                                val sendIntent: Intent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, "¡Únete a mi grupo familiar en Mi Mercado! Código: ${household.joinCode}")
                                                    type = "text/plain"
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, null))
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Compartir")
                                        }
                                    }

                                    if (uiState.isCurrentUserAdult) {
                                        Spacer(Modifier.height(12.dp))
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

                // PANEL SECUNDARIO: Unirse a otra familia (Siempre visible o como alternativa)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Unirse a otra familia", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Ingresa el código de 6 caracteres para transferirte.", color = Color.Gray, fontSize = 13.sp)
                            
                            Spacer(Modifier.height(12.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = uiState.joinCodeInput,
                                    onValueChange = { if (it.length <= 6) viewModel.onJoinCodeInputChanged(it) },
                                    label = { Text("Código") },
                                    leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Button(
                                    onClick = { viewModel.joinHouseholdWithCode() },
                                    enabled = uiState.joinCodeInput.trim().length == 6 && !uiState.isJoining,
                                    shape = RoundedCornerShape(8.dp),
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

                // MIEMBROS (Si hay hogar)
                if (hasHousehold) {
                    item {
                        Text("Integrantes actuales", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
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

@Composable
fun MemberCardRow(
    member: HouseholdMember, 
    isCurrentUser: Boolean,
    isEditable: Boolean, 
    onRoleChange: (MemberRole) -> Unit,
    onAvatarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(enabled = isCurrentUser, onClick = onAvatarClick),
                shape = CircleShape, 
                color = MaterialTheme.colorScheme.secondaryContainer,
                border = if (isCurrentUser) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = AvatarUtils.getAvatarEmoji(member.avatarIcon),
                        fontSize = 24.sp
                    )
                    if (isCurrentUser) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        }
                    }
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = if (isCurrentUser) "${member.displayName} (Tú)" else member.displayName, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 16.sp
                )
                Text(
                    text = if (member.role == MemberRole.ADULT) "Adulto (Comprador)" else "Menor (Sugeridor)",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
            
            Switch(
                checked = member.role == MemberRole.ADULT,
                enabled = isEditable && !isCurrentUser, 
                onCheckedChange = { isAdult ->
                    onRoleChange(if (isAdult) MemberRole.ADULT else MemberRole.CHILD)
                }
            )
        }
    }
}

@Composable
fun AvatarPickerDialog(
    onDismiss: () -> Unit,
    onAvatarSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Elige tu Avatar", fontWeight = FontWeight.Bold) },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(240.dp)
            ) {
                items(AvatarUtils.avatars) { avatar ->
                    Surface(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { onAvatarSelected(avatar.id) },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(avatar.emoji, fontSize = 32.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
