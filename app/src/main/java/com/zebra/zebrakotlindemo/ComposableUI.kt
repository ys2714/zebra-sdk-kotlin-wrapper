package com.zebra.zebrakotlindemo

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Menu

@Composable
fun RoundButton(title: String, modifier: Modifier? = null, onClick: () -> Unit) {
    if (modifier != null) {
        Button(
            modifier = modifier
            , onClick = {
                onClick()
            }
            , colors = ButtonColors(
                containerColor = Color.Blue,
                contentColor = Color(0xFFFFFFFF),
                disabledContentColor = Color.White,
                disabledContainerColor = Color.LightGray
            )
        ) {
            Text(text = title)
        }
    } else {
        Button(
            modifier = Modifier
                .fillMaxWidth()
            , onClick = {
                onClick()
            }
            , colors = ButtonColors(
                containerColor = Color.Blue,
                contentColor = Color(0xFFFFFFFF),
                disabledContentColor = Color.White,
                disabledContainerColor = Color.LightGray
            )
        ) {
            Text(text = title)
        }
    }
}

@Composable
fun StyledOutlinedTextField(
    placeholder: String = "scan barcode or manually input",
    currentValue: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    valueChangeAction: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = currentValue,
        onValueChange = {
            valueChangeAction(it)
            // focusManager.clearFocus()
        },
        modifier = modifier, // Take up the full width
        label = { Text("Item number") },
        placeholder = { Text(placeholder) },
        singleLine = true, // Prevent multi-line input

        // Add a clear button to the end of the text field
        trailingIcon = {
            if (currentValue.isNotEmpty()) {
                IconButton(onClick = { valueChangeAction("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear text"
                    )
                }
            }
        },

        // Configure the keyboard
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number, // Use the email keyboard
            imeAction = ImeAction.Done // Show a "Done" button
        )
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}