package com.saico.mimercado.feature.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.saico.mimercado.core.ui.R
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    onSignInSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSignInSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenido a Mi Mercado", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))
        
        if (uiState.error != null) {
            Text(uiState.error!!, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
        }

        Button(
            onClick = {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                scope.launch {
                    try {
                        Log.d("SignIn", "🚀 Iniciando getCredential...")
                        val result = credentialManager.getCredential(context, request)
                        val credential = result.credential
                        
                        Log.d("SignIn", "✉️ Credencial obtenida tipo: ${credential::class.java.name}")
                        
                        if (credential is GoogleIdTokenCredential) {
                            Log.d("SignIn", "✅ Credencial es GoogleIdTokenCredential de forma nativa")
                            viewModel.onGoogleSignInResult(credential.idToken)
                        } else {
                            // Intentar parsear de forma explícita si llega envuelta en CustomCredential o similar
                            try {
                                val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                                Log.d("SignIn", "✅ Token extraído exitosamente usando createFrom(credential.data)")
                                viewModel.onGoogleSignInResult(idToken)
                            } catch (parseException: Exception) {
                                Log.e("SignIn", "❌ No se pudo extraer GoogleIdToken del tipo de credencial recibido: ${parseException.message}")
                            }
                        }
                    } catch (e: GetCredentialException) {
                        Log.e("SignIn", "❌ getCredential falló: ${e::class.simpleName} - ${e.message}", e)
                    } catch (e: Exception) {
                        Log.e("SignIn", "❌ Falló después de obtener credencial: ${e.message}", e)
                    }
                }
            },
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Continuar con Google")
            }
        }
    }
}
