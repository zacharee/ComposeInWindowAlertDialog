@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE")

package dev.zwander.compose.alertdialog

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

/**
 * Delegate the popup logic to the platform.
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
internal actual fun PlatformAlertDialog(
    showing: Boolean,
    onDismissRequest: () -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier,
    title: @Composable (() -> Unit)?,
    text: @Composable (ColumnScope.() -> Unit)?,
    shape: Shape,
    backgroundColor: Color,
    contentColor: Color,
    maxWidth: Dp,
    windowDecorations: DpRect,
) {
    val alpha by animateFloatAsState(
        targetValue = if (showing) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "DialogFade",
    )
    val showingForAnimation by derivedStateOf {
        alpha > 0f
    }
    val properties = PopupProperties(
        focusable = true,
        dismissOnClickOutside = true,
        dismissOnBackPress = true,
        usePlatformInsets = false,
    )
    val focusRequester = remember { FocusRequester() }
    val safeAreaInsets = WindowInsets.safeContent.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current

    val safeAreaStart = safeAreaInsets.calculateStartPadding(layoutDirection)
    val safeAreaEnd = safeAreaInsets.calculateEndPadding(layoutDirection)

    LaunchedEffect(showing, alpha, showingForAnimation, focusRequester.focusRequesterNodes.size) {
        if (showing && alpha == 1f && focusRequester.focusRequesterNodes.isNotEmpty()) {
            focusRequester.requestFocus()
        }

        if (!showing && showingForAnimation) {
            focusRequester.freeFocus()
        }
    }

    if (showing || showingForAnimation) {
        Popup(
            alignment = Alignment.Center,
            properties = properties,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f * alpha))
                    .fillMaxSize()
                    .onClick { onDismissRequest() }
                    .padding(
                        start = safeAreaStart + windowDecorations.left,
                        top = safeAreaInsets.calculateTopPadding().takeIf { it > 0.dp } ?: (16.dp + windowDecorations.bottom),
                        end = safeAreaEnd + windowDecorations.right,
                        bottom = safeAreaInsets.calculateBottomPadding().takeIf { it > 0.dp } ?: (16.dp + windowDecorations.top),
                    )
                    .alpha(alpha)
                    .onPreviewKeyEvent {
                        if (it.key == Key.Escape) {
                            onDismissRequest()
                            true
                        } else {
                            false
                        }
                    }
                    .focusable(true)
                    .focusRequester(focusRequester),
                contentAlignment = Alignment.Center,
            ) {
                with(LocalDensity.current) {
                    AlertDialogContents(
                        buttons,
                        modifier.then(
                            Modifier.widthIn(
                                max = minOf(
                                    constraints.maxWidth.toDp() - (32.dp.takeIf { safeAreaStart <= 0.dp && safeAreaEnd <= 0.dp } ?: 0.dp),
                                    maxWidth,
                                )
                            ).onClick {
                                // To prevent the Box's onClick consuming clicks on the dialog itself.
                            }.animateContentSize(),
                        ),
                        title,
                        text,
                        shape,
                        backgroundColor,
                        contentColor,
                    )
                }
            }
        }
    }
}