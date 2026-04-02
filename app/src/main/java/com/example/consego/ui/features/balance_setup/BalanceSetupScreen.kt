package com.example.consego.ui.features.balance_setup


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceSetupScreen(onComplete: (Double, Double) -> Unit) {
    var cashAmount by remember { mutableStateOf("") }
    var bankAmount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Setup Your Balance",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )

        Text(
            text = "How much money do you currently have?",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        // Cash Input Card
        BalanceInputCard(
            label = "Cash on hand",
            value = cashAmount,
            onValueChange = { cashAmount = it },
            placeholder = "0.00"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bank Input Card
        BalanceInputCard(
            label = "Bank Balance",
            value = bankAmount,
            onValueChange = { bankAmount = it },
            placeholder = "0.00"
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val cash = cashAmount.toDoubleOrNull() ?: 0.0
                val bank = bankAmount.toDoubleOrNull() ?: 0.0
                onComplete(cash, bank)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF744BD7))
        ) {
            Text("Confirm & Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BalanceInputCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = label, fontSize = 12.sp, color = Color.Gray)
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = { Text(placeholder, color = Color.LightGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}