package dev.zwander.compose.alertdialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp

val LocalWindowDecorations = compositionLocalOf { DpRect(0.dp, 0.dp, 0.dp, 0.dp) }

/**
 * An implementation of AlertDialog that doesn't open a separate window on desktop platforms.
 */
@Suppress("unused")
@Composable
fun InWindowAlertDialog(
    showing: Boolean,
    onDismissRequest: () -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)?,
    text: (@Composable ColumnScope.() -> Unit)?,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    maxWidth: Dp = 400.dp,
    windowDecorations: DpRect = LocalWindowDecorations.current,
) {
    PlatformAlertDialog(
        showing = showing,
        onDismissRequest = onDismissRequest,
        buttons = buttons,
        modifier = modifier,
        title = title,
        text = text,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        maxWidth = maxWidth,
        windowDecorations = windowDecorations,
    )
}

/**
 * Delegate the popup logic to the platform.
 */
@Composable
internal expect fun PlatformAlertDialog(
    showing: Boolean,
    onDismissRequest: () -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier,
    title: (@Composable () -> Unit)?,
    text: (@Composable ColumnScope.() -> Unit)?,
    shape: Shape,
    backgroundColor: Color,
    contentColor: Color,
    maxWidth: Dp,
    windowDecorations: DpRect = DpRect(0.dp, 0.dp, 0.dp, 0.dp),
)

@Composable
internal fun AlertDialogContents(
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)?,
    text: (@Composable ColumnScope.() -> Unit)?,
    shape: Shape = RoundedCornerShape(8.dp),
    backgroundColor: Color,
    contentColor: Color,
) {
    Surface(
        shape = shape,
        modifier = modifier,
        color = backgroundColor,
        contentColor = contentColor,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            title?.let {
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                    val textStyle = MaterialTheme.typography.headlineSmall
                    ProvideTextStyle(textStyle) {
                        it()
                    }
                }
            }

            text?.let {
                Column(modifier = Modifier.weight(1f, false)) {
                    it()
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.weight(1f))

                buttons()
            }
        }
    }
}
