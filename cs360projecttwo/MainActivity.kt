package com.example.cs360projecttwo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.cs360projecttwo.database.InventoryDatabaseHelper
import com.example.cs360projecttwo.database.InventoryItem
import com.example.cs360projecttwo.ui.theme.CS360_ProjectTwoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CS360_ProjectTwoTheme {
                InventoryScreen()
            }
        }
    }
}

@Composable
fun InventoryScreen() {
    val context = LocalContext.current
    val dbHelper = remember { InventoryDatabaseHelper(context) }

    var itemName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var phoneNumber by remember {
        mutableStateOf(
            context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .getString("savedPhoneNumber", "") ?: ""
        )
    }
    var items by remember { mutableStateOf(dbHelper.getAllItems()) }

    val hasSmsPermission = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED
    }

    val requestSmsPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(context, "SMS permission denied!", Toast.LENGTH_SHORT).show()
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // FIXED: Ensure `.dp` is used
    ) {
        Text("Inventory Manager", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp)) // FIXED: Ensure `.dp` is used

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("savedPhoneNumber", phoneNumber)
                    .apply()
                Toast.makeText(context, "Phone number saved!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Phone Number")
        }

        if (!hasSmsPermission) {
            Button(
                onClick = { requestSmsPermissionLauncher.launch(Manifest.permission.SEND_SMS) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enable SMS Notifications")
            }
        }

        OutlinedTextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (itemName.isNotBlank() && quantity.isNotBlank()) {
                    val qty = quantity.toIntOrNull() ?: return@Button
                    dbHelper.addItem(itemName, qty)
                    items = dbHelper.getAllItems()
                    itemName = ""
                    quantity = ""
                    Toast.makeText(context, "Item Added!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Item")
        }

        LazyColumn {
            items(items) { item ->
                InventoryItemCard(item, context, phoneNumber) {
                    dbHelper.deleteItem(item.id)
                    items = dbHelper.getAllItems()
                }
            }
        }
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    context: Context,
    phoneNumber: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp) // FIXED: `.dp` added
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium)
            Text("Quantity: ${item.quantity}")

            Button(onClick = onDelete) {
                Text("Delete")
            }

            if (item.quantity <= 5) {
                sendSmsNotification(context, item.name, item.quantity, phoneNumber)
                sendPushNotification(context, item.name, item.quantity)
            }
        }
    }
}

private fun sendSmsNotification(context: Context, itemName: String, quantity: Int, phoneNumber: String) {
    try {
        if (phoneNumber.isBlank() ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

        smsManager.sendTextMessage(
            phoneNumber, null,
            "Low stock alert: $itemName has only $quantity left!",
            null, null
        )
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to send SMS: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun sendPushNotification(context: Context, itemName: String, quantity: Int) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "inventory_notifications",
                "Inventory Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "inventory_notifications")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Low Stock Alert")
            .setContentText("$itemName has only $quantity items remaining")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(1, notification)
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to send notification: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
