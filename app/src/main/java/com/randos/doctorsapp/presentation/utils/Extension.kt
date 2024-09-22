package com.randos.doctorsapp.presentation.utils

import android.content.res.Resources
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.randos.domain.model.Address
import java.time.LocalDateTime

fun Int.pxToDp(): Dp {
    return (this / Resources.getSystem().displayMetrics.density).dp
}

fun LocalDateTime.stringFormat(): String {
    return "$hour:$minute"
}

fun Address.stringFormat(): String {
    return "$street, $city, $state, $country $postalCode"
}