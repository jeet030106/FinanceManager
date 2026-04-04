package com.example.consego.ui.features.transaction_history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.consego.data.model.TransactionEntity
import com.example.consego.data.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    onEditNavigate: (Int) -> Unit,
    viewModel: TransactionHistoryViewModel = hiltViewModel()
) {
    val transactions by viewModel.filteredTransactions.collectAsState()
    val currentFilter by viewModel.selectedFilter.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "History",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Filter Chips with Purple Theme
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TransactionFilter.entries.forEach { filter ->
                val isSelected = currentFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateFilter(filter) },
                    label = {
                        Text(
                            text = filter.name,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF744BD7),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF5F5F5),
                        labelColor = Color.Gray
                    ),
                    border = null,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List with Shadowed Boxes
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(transactions, key = { it.id }) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onEdit = { onEditNavigate(transaction.id) },
                    onDelete = { viewModel.deleteTransaction(transaction) }
                )
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp // Box with Shadow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.Black
                )
                if (transaction.notes.isNotEmpty()) {
                    Text(
                        text = transaction.notes,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
                Text(
                    text = transaction.accountType,
                    fontSize = 11.sp,
                    color = Color(0xFF744BD7),
                    fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (transaction.type == TransactionType.INCOME) "+$${transaction.amount}" else "-$${transaction.amount}",
                    color = if (transaction.type == TransactionType.INCOME) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp
                )

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF744BD7),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF8A8A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}