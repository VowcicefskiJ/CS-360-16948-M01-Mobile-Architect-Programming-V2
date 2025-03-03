package com.example.cs360projecttwo.ui.theme

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cs360projecttwo.database.InventoryDatabaseHelper

@Composable
fun InventoryScreen() {
    val context = LocalContext.current
    val dbHelper = remember { InventoryDatabaseHelper(context) }
    var items by remember { mutableStateOf(dbHelper.getAllItems()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Inventory List", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(items) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.name}: ${item.quantity}")
                        Button(onClick = {
                            dbHelper.deleteItem(item.id)
                            items = dbHelper.getAllItems()
                        }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}
