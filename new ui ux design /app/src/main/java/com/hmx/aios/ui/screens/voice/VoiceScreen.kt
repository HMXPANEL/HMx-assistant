package com.hmx.aios.ui.screens.voice

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmx.aios.ui.theme.*
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun VoiceScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.GraphicEq, contentDescription = "Voice", tint = NeonGreen)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("VOICE ASSISTANT", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("HANDS-FREE INTERFACE", color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
            }
            Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = TextSecondary)
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            com.hmx.aios.ui.components.AICoreOrb(modifier = Modifier.size(240.dp))
        }
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("LISTENING", color = NeonGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Say \"Hey Max\" to wake me up", color = TextSecondary, fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(16.dp))
                .border(1.dp, SurfaceVariantDark, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("DETECTION HISTORY", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("See All", color = NeonGreen, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            DetectionItem("Hey Max", "Today, 09:30 AM")
            DetectionItem("Hey Max, call Mom", "Today, 08:10 AM")
            DetectionItem("Hey Max, set alarm", "Yesterday, 10:15 PM")
            DetectionItem("What's the time?", "Yesterday, 09:00 PM")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { Toast.makeText(context, "Listening stopped", Toast.LENGTH_SHORT).show() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = NeonGreen),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
        ) {
            Icon(Icons.Default.StopCircle, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("STOP LISTENING", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun DetectionItem(text: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            com.hmx.aios.ui.components.Glowing3DIcon(Icons.Default.GraphicEq, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text, color = TextPrimary, fontSize = 14.sp)
                Text(time, color = TextSecondary, fontSize = 12.sp)
            }
        }
        com.hmx.aios.ui.components.Glowing3DIcon(Icons.Default.GraphicEq, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
    }
}
