// RegistrationScreen.kt
package com.example.jvent.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jvent.R
import com.example.jvent.components.DefaultTopBar
import com.example.jvent.viewmodel.RegistrationViewModel

@Composable
fun RegistrationScreen(
    navController: NavController,
    viewModel: RegistrationViewModel = viewModel()
) {
    val context = LocalContext.current

    // Show error toast when error occurs
    LaunchedEffect(viewModel.error) {
        viewModel.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            DefaultTopBar(title = stringResource(id = R.string.app_name))
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.make_account),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.register_to_make_event),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                OutlinedTextField(
                    value = viewModel.username,
                    onValueChange = viewModel::updateUsername,
                    label = { Text(stringResource(R.string.username)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.error != null
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = viewModel::updateEmail,
                    label = { Text(stringResource(R.string.email)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.error != null
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = viewModel::updatePassword,
                    label = { Text(stringResource(R.string.password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.error != null
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                OutlinedTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = viewModel::updateConfirmPassword,
                    label = { Text(stringResource(R.string.confirm_password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.error != null
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Button(
                    onClick = {
                        viewModel.register(
                            onSuccess = {
                                navController.navigate("login") {
                                    popUpTo("registration") { inclusive = true }
                                }
                            },
                            onError = {
                                // Error is already shown via LaunchedEffect
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(stringResource(R.string.register))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TextButton(
                    onClick = { navController.navigate("login") },
                    enabled = !viewModel.isLoading
                ) {
                    Text(
                        text = stringResource(R.string.have_account),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}