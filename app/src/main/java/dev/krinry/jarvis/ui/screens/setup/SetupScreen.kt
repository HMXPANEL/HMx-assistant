package dev.krinry.jarvis.ui.screens.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.krinry.jarvis.ui.theme.*
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun SetupScreen() {
    val context = LocalContext.current
    var selectedProvider by remember { 
        mutableStateOf(
            dev.krinry.jarvis.security.SecureKeyStore.getSelectedProvider(context) ?: "gemini"
        ) 
    }
    var apiKeyInput by remember { mutableStateOf(
        dev.krinry.jarvis.security.SecureKeyStore.getProviderApiKey(context, selectedProvider) ?: ""
    ) }
    var expanded by remember { mutableStateOf(false) }
    val providers = listOf("gemini", "groq", "openrouter")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("SETUP", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("CONFIGURE YOUR AI OS", color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
                }
            }
        }
        
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark, RoundedCornerShape(16.dp))
                    .border(1.dp, SurfaceVariantDark, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = "Config", tint = NeonGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("AGENT CONFIGURATION", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Customize how Max works on your device.", color = TextSecondary, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceVariantDark, RoundedCornerShape(12.dp))
                            .clickable { expanded = true }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selectedProvider.uppercase(), color = TextPrimary, fontSize = 14.sp)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = NeonGreen)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(SurfaceDark)
                    ) {
                        providers.forEach { provider ->
                            DropdownMenuItem(
                                text = { Text(provider.uppercase(), color = TextPrimary) },
                                onClick = {
                                    selectedProvider = provider
                                    apiKeyInput = dev.krinry.jarvis.security.SecureKeyStore
                                        .getProviderApiKey(context, provider) ?: ""
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "API KEY",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your API key...", color = TextSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = SurfaceVariantDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = NeonGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SettingItem(Icons.Outlined.Psychology, "AI Model", "Gemini Pro 1.5")
                SettingItem(Icons.Outlined.RecordVoiceOver, "Voice Model", "Neural 2.0")
                SettingItem(Icons.Outlined.Mic, "Wake Word", "Hey Max")
                SettingItem(Icons.Outlined.Language, "Language", "English (US)")
                SettingItem(Icons.Outlined.ChatBubbleOutline, "Response Style", "Balanced")
                SettingItem(Icons.Outlined.AutoMode, "Autonomous Actions", "Enabled")
                SettingItem(Icons.Outlined.Layers, "Overlay Permission", "Enabled")
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        if (apiKeyInput.isNotBlank()) {
                            dev.krinry.jarvis.security.SecureKeyStore.setProviderApiKey(
                                context, selectedProvider, apiKeyInput.trim()
                            )
                            dev.krinry.jarvis.security.SecureKeyStore.setSelectedProvider(context, selectedProvider)
                            Toast.makeText(context, "✅ API Key saved successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "⚠️ Please enter an API key", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = DarkBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("SAVE CHANGES", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}

@Composable
fun SettingItem(icon: ImageVector, label: String, value: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { Toast.makeText(context, "$label tapped", Toast.LENGTH_SHORT).show() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, color = TextPrimary, fontSize = 14.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
        }
    }
}