package com.example.remeeton.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun MessageHandler(
    messageSuccess: String?,
    messageError: String?
) {
    val context = LocalContext.current

    messageSuccess?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }

    messageError?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }
}
