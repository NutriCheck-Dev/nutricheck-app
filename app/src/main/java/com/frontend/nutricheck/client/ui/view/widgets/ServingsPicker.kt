package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.floor

/**
 * A composable function that displays a servings picker.
 *
 * @param value The current number of servings selected.
 * @param onValueChange Callback function to be invoked when the selected number of servings
 */
@Composable
fun ServingsPicker(
    modifier: Modifier = Modifier,
    value: Double,
    onValueChange: (Double) -> Unit,
    min: Double = 0.0,
    max: Double = 200.0,
    decimals: Int = 2
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography

    val intSLots = remember(max) { floor(max).toInt().toString().length }
    val maxDigits = intSLots + decimals
    val minScaled = remember(min, decimals) { toScaledLong(min, decimals) }
    val maxScaled = remember(max, decimals) { toScaledLong(max, decimals) }

    var rawDigits by remember(value, decimals, maxDigits) {
        val scaled = toScaledLong(value, decimals)
        mutableStateOf(scaledLongToDigits(scaled, decimals, intSLots))
    }

    var textFieldValue by remember(rawDigits) {
        mutableStateOf(TextFieldValue(rawDigits, selection = TextRange(rawDigits.length)))
    }

    var errorText by remember { mutableStateOf<String?>(null) }
    val rangeErrorMessage = stringResource(R.string.error_servings_range,formatDouble(min, decimals), formatDouble(max, decimals))

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newField ->
            val proposedDigits = newField.text.filter(Char::isDigit)
            val proposedScaled = proposedDigits.ifEmpty { "0" }.toLong()

            if (proposedScaled > maxScaled) {
                errorText = rangeErrorMessage
                textFieldValue = textFieldValue.copy(selection = TextRange(textFieldValue.text.length))
                return@OutlinedTextField
            }

            val digits = clampDigits(proposedDigits, maxDigits)
            val scaled = digitsToScaledLong(digits, decimals, intSLots)

            rawDigits = digits
            textFieldValue = TextFieldValue(digits, selection = TextRange(digits.length))

            errorText = if (scaled < minScaled && scaled != 0L) {
                rangeErrorMessage
            } else null

            if (scaled in minScaled..maxScaled) onValueChange(scaledLongToDouble(scaled, decimals))
        },
        singleLine = true,
        modifier = modifier.width(180.dp),
        textStyle = styles.bodyLarge.copy(
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
            color = colors.onSurfaceVariant
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = FixedDecimalMaskTransformation(decimals),
        placeholder = { Text(formatDouble(0.0, decimals)) },
        isError = errorText != null,
        supportingText = {
            errorText?.let { Text(errorText!!, color = colors.error, style = styles.bodySmall) }
        },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceVariant,
            unfocusedContainerColor = colors.surfaceVariant,
            disabledContainerColor = colors.surfaceVariant,
            focusedBorderColor = colors.outline,
            unfocusedBorderColor = colors.outline,
            errorBorderColor = colors.error,
            focusedTextColor = colors.onSurfaceVariant,
            unfocusedTextColor = colors.onSurfaceVariant,
            errorTextColor = colors.onSurfaceVariant
        )
    )

}

fun toScaledLong(value: Double, decimals: Int): Long =
    BigDecimal.valueOf(value)
        .movePointRight(decimals)
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()

fun scaledLongToDouble(scaled: Long, decimals: Int): Double =
    BigDecimal(scaled)
        .movePointLeft(decimals)
        .toDouble()

fun digitsToScaledLong(digits: String, decimals: Int, intSlots: Int): Long {
    val maxDigits = intSlots + decimals
    val clamped = clampDigits(digits.filter(Char::isDigit), maxDigits)
    return clamped.ifEmpty { "0" }.toLong()
}

fun scaledLongToDigits(scaled: Long, decimals: Int, intSlots: Int): String {
    val maxDigits = intSlots + decimals
    val scaledDigit = scaled.coerceAtLeast(0L).toString()
    return clampDigits(scaledDigit, maxDigits)
}

private fun clampDigits(digits: String, maxDigits: Int): String =
    if (digits.length <= maxDigits) digits else digits.takeLast(maxDigits)

private fun formatDouble(value: Double, decimals: Int): String =
    BigDecimal.valueOf(value).setScale(decimals, RoundingMode.HALF_UP).toPlainString()

private class FixedDecimalMaskTransformation(
    private val decimals: Int
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter(Char::isDigit)

        val padded = digits.padStart(decimals + 1, '0')

        val intPart = padded.dropLast(decimals)
        val fracPart = padded.takeLast(decimals).padEnd(decimals, '0')

        val formatted = if (decimals > 0) "$intPart.$fracPart" else intPart

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = formatted.length
            override fun transformedToOriginal(offset: Int): Int = digits.length
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}