package com.example.consego.ui.features.add_transaction

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.consego.data.model.TransactionType
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionId: Int? = null, // Added for Edit Mode
    onBack: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isEditMode = transactionId != null

    // Trigger load when screen opens with an ID for editing
    LaunchedEffect(transactionId) {
        if (isEditMode && transactionId != null) {
            viewModel.loadTransaction(transactionId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isEditMode) "Edit Transaction" else "Add Transaction",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Amount Input
        OutlinedTextField(
            value = uiState.amount,
            onValueChange = viewModel::onAmountChange,
            label = { Text("Amount") },
            prefix = { Text("$ ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Transaction Type Toggle
        Text("Transaction Type", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            TransactionType.entries.forEach { type -> // Use .entries for Kotlin 1.9+
                val isSelected = uiState.type == type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF744BD7) else Color.Transparent)
                        .clickable { viewModel.onTypeChange(type) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = type.title,
                        color = if (isSelected) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Payment Method Toggle
        Text("Payment Method", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            listOf("CASH", "BANK").forEach { method ->
                val isSelected = uiState.accountType == method
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF744BD7) else Color.Transparent)
                        .clickable { viewModel.onAccountTypeChange(method) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = method,
                        color = if (isSelected) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Category Dropdown
        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = uiState.category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF744BD7),
                    unfocusedBorderColor = Color.Gray
                )
            )
            // Overlay Box to make the entire field clickable for the dropdown
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                viewModel.getCategories().forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            viewModel.onCategoryChange(cat)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes Input
        OutlinedTextField(
            value = uiState.notes,
            onValueChange = viewModel::onNotesChange,
            label = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Save/Update Button
        Button(
            onClick = {
                viewModel.saveTransaction(
                    onSuccess = { onBack() },
                    onError = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF744BD7))
        ) {
            Text(
                text = if (isEditMode) "Update Transaction" else "Save Transaction",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}