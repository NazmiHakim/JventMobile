package com.example.jvent.screen

import com.example.jvent.viewmodel.LoginViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jvent.components.DefaultTopBar
import com.example.jvent.R


@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: () -> Unit,
    navigateToRegistration: () -> Unit,
    viewModel: LoginViewModel = viewModel() // Regular ViewModel
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
                    text = stringResource(id = R.string.login_title),
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Show error message if exists
                viewModel.error?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = viewModel::updateEmail,
                    label = { Text(stringResource(id = R.string.email)) },
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
                    label = { Text(stringResource(id = R.string.password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.error != null
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Button(
                    onClick = {
                        viewModel.login(
                            onSuccess = {
                                onLoginSuccess()
                                navController.navigate("dashboard") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
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
                        Text(text = stringResource(id = R.string.login))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TextButton(
                    onClick = navigateToRegistration,
                    enabled = !viewModel.isLoading
                ) {
                    Text(
                        text = stringResource(id = R.string.dont_have_account),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
