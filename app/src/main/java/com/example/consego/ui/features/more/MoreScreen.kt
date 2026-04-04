package com.example.consego.ui.features.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MoreScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "More",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {


            // 2. Support & Feedback Section
            item { SectionHeader("Support & Feedback") }
            item {
                MoreOptionItem(Icons.Default.Email, "Send Feedback") { /* Handle Feedback */ }
            }
            item {
                MoreOptionItem(Icons.Default.Info, "Frequently Asked Questions") { /* Handle FAQ */ }
            }
            item {
                MoreOptionItem(Icons.Default.Call, "Customer Support") { /* Handle Support */ }
            }
            item {
                MoreOptionItem(Icons.Default.Share, "Share Application") { /* Handle Support */ }
            }
            item {
                MoreOptionItem(Icons.Default.Info, "About Us") { /* Handle Support */ }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Version 1.0.42",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}


@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.Gray,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp, start = 4.dp)
    )
}

@Composable
fun MoreOptionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF9F9F9))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF744BD7),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}