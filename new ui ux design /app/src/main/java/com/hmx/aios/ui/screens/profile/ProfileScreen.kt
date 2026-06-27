package com.hmx.aios.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmx.aios.ui.theme.*
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
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
                Icon(Icons.Default.PersonSearch, contentDescription = "Profile", tint = TextPrimary)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("PROFILE", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("YOUR AI IDENTITY", color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
                }
            }
        }
        
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                        .border(2.dp, NeonGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    com.hmx.aios.ui.components.Glowing3DIcon(Icons.Default.Person, contentDescription = "Avatar", tint = NeonGreen, modifier = Modifier.size(60.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Anuj", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Premium User", color = TextSecondary, fontSize = 14.sp)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("142", "COMMANDS")
                    StatItem("94%", "SUCCESS RATE")
                    StatItem("56h", "TIME SAVED")
                }
            }
        }
        
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark, RoundedCornerShape(16.dp))
                    .border(1.dp, SurfaceVariantDark, RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp)
            ) {
                ProfileMenuItem(Icons.Outlined.Person, "Account Settings")
                ProfileMenuItem(Icons.Outlined.Palette, "Appearance")
                ProfileMenuItem(Icons.Outlined.Lock, "Data & Privacy")
                ProfileMenuItem(Icons.Outlined.Link, "Linked Accounts")
                ProfileMenuItem(Icons.Outlined.Backup, "Backup & Restore")
                ProfileMenuItem(Icons.Outlined.Info, "About Max", "v2.1.0")
            }
        }
        
        item {
            Button(
                onClick = { Toast.makeText(context, "Session disconnected", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("DISCONNECT SESSION", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, value: String? = null) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { Toast.makeText(context, "$label tapped", Toast.LENGTH_SHORT).show() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, color = TextPrimary, fontSize = 15.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value != null) {
                Text(value, color = TextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}
