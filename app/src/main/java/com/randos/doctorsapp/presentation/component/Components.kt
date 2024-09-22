package com.randos.doctorsapp.presentation.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.randos.doctorsapp.presentation.utils.pxToDp


@Composable
fun NetworkActionButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    var buttonHeight by remember { mutableStateOf(0.dp) }
    Button(
        modifier = modifier.onSizeChanged {
            if (buttonHeight == 0.dp) {
                buttonHeight = it.height.pxToDp().minus(24.dp)
            }
        },
        enabled = enabled && !isLoading,
        onClick = onClick
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(buttonHeight))
        } else {
            content()
        }
    }
}
