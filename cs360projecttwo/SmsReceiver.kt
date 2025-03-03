package com.example.cs360projecttwo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val sender = message.displayOriginatingAddress ?: "Unknown"
                val messageBody = message.displayMessageBody

                // Display a toast message when an SMS is received
                Toast.makeText(context, "SMS from $sender: $messageBody", Toast.LENGTH_LONG).show()
            }
        }
    }
}
