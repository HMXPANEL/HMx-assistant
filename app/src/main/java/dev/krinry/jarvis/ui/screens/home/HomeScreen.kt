package dev.krinry.jarvis.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import dev.krinry.jarvis.ui.theme.*
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { HeaderSection() }
        item { SystemHealthSection() }
        item { ActiveModulesSection() }
        item { QuickCommandsSection() }
        item { RecentCommandsSection(context) }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "HMx",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "AI OPERATING SYSTEM",
                fontSize = 12.sp,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NeonGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ONLINE & READY",
                    color = NeonGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }
        
        // AI Core Orb Animation
        dev.krinry.jarvis.ui.components.AICoreOrb(modifier = Modifier.size(72.dp))
    }
}

@Composable
fun SystemHealthSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SYSTEM HEALTH",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Text(text = "OPTIMAL", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HealthMetric("CPU", "12%")
            HealthMetric("RAM", "43%")
            HealthMetric("STORAGE", "62%")
            HealthMetric("NETWORK", "98%", isHigh = true)
        }
    }
}

@Composable
fun HealthMetric(label: String, value: String, isHigh: Boolean = false) {
    Column(
        modifier = Modifier
            .background(SurfaceDark, RoundedCornerShape(12.dp))
            .border(1.dp, SurfaceVariantDark, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .width(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, color = TextSecondary, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = if (isHigh) NeonGreen else TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ActiveModulesSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ACTIVE MODULES",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Icon(Icons.Default.ArrowForward, contentDescription = "See All", tint = NeonGreen, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ModuleItem("Voice", Icons.Default.Mic)
            ModuleItem("Vision", Icons.Default.Visibility)
            ModuleItem("Memory", Icons.Default.Memory)
            ModuleItem("Tasks", Icons.Default.TaskAlt)
            ModuleItem("+2", null, isAdd = true)
        }
    }
}

@Composable
fun ModuleItem(label: String, icon: ImageVector?, isAdd: Boolean = false) {
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { Toast.makeText(context, "$label module opened", Toast.LENGTH_SHORT).show() }) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(if (isAdd) SurfaceVariantDark else SurfaceDark, RoundedCornerShape(12.dp))
                .border(1.dp, if (isAdd) NeonGreen.copy(alpha = 0.3f) else SurfaceVariantDark, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                dev.krinry.jarvis.ui.components.Glowing3DIcon(icon, contentDescription = label, tint = NeonGreen, modifier = Modifier.size(20.dp))
            } else {
                Text(text = label, color = NeonGreen, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (!isAdd) {
            Text(text = label, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun QuickCommandsSection() {
    Column {
        Text(
            text = "QUICK COMMAND",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CommandItem("New Chat", Icons.Default.ChatBubbleOutline)
            CommandItem("Voice Mode", Icons.Default.MicNone)
            CommandItem("Open App", Icons.Default.Apps)
            CommandItem("Tools", Icons.Default.Build)
        }
    }
}

@Composable
fun CommandItem(label: String, icon: ImageVector) {
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { Toast.makeText(context, "Executing: $label", Toast.LENGTH_SHORT).show() }) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(SurfaceDark, CircleShape)
                .border(1.dp, SurfaceVariantDark, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            dev.krinry.jarvis.ui.components.Glowing3DIcon(icon, contentDescription = label, tint = NeonGreen, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, color = TextSecondary, fontSize = 10.sp, maxLines = 1)
    }
}

@Composable
fun RecentCommandsSection(context: android.content.Context) {
    val prefs = context.getSharedPreferences("hmx_history", android.content.Context.MODE_PRIVATE)
    val historyJson = prefs.getString("recent_commands", null)
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT COMMANDS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Icon(Icons.Default.ArrowForward, contentDescription = "More", tint = NeonGreen, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        if (historyJson.isNullOrEmpty()) {
            Text(
                text = "No commands yet. Say 'Hey Max' or use Chat to get started.",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            try {
                val arr = org.json.JSONArray(historyJson)
                val count = minOf(arr.length(), 3)
                for (i in 0 until count) {
                    val obj = arr.getJSONObject(i)
                    RecentCommandItem(
                        icon = Icons.Default.Terminal,
                        text = obj.getString("command"),
                        time = obj.getString("time"),
                        iconTint = NeonGreen
                    )
                    if (i < count - 1) Spacer(modifier = Modifier.height(12.dp))
                }
            } catch (e: Exception) {
                Text("No recent commands", color = TextSecondary, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun RecentCommandItem(icon: ImageVector, text: String, time: String, iconTint: Color) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(16.dp))
            .clickable { Toast.makeText(context, "Running: $text", Toast.LENGTH_SHORT).show() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconTint.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            dev.krinry.jarvis.ui.components.Glowing3DIcon(icon, contentDescription = text, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = time, color = TextSecondary, fontSize = 12.sp)
    }
}