# Compose In-Window AlertDialog
An alternative implementation of AlertDialog for Compose Multiplatform that shows an Android-style popup dialog with fade-in and fade-out animations.

Supported platforms:
- Android
- JVM
- iOS
- macOS native
- JS
- Web Assembly

## Installation
![Maven Central Version](https://img.shields.io/maven-central/v/dev.zwander/composedialog)

Add the dependency to your `commonMain` source set:

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("dev.zwander:composedialog:VERSION")
        }
    }
}
```

## Usage
Use the `InWindowAlertDialog` Composable in your code.

```kotlin
var showingDialog by remember {
    mutableStateOf(false)
}

Surface {
    Column {
        // ...
    }
}

InWindowAlertDialog(
    showing = showingDialog,
    onDismissRequest = { showingDialog = false },
    title = { Text(text = "Title") },
    text = { Text(text = "Message") },
    buttons = {
        TextButton(
            onClick = { 
                // ...
                showingDialog = false
            },
        ) {
            Text(text = "OK")
        }
    },
    // Optional
    shape = MaterialTheme.shapes.extraLarge,
    // Optional
    backgroundColor = MaterialTheme.colorScheme.surface,
    // Optional
    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceVariant),
    // Optional
    maxWidth = 400.dp,
    // Optional
    windowDecorations = LocalWindowDecorations.current,
    // Optional
    modifier = Modifier,
)
```

If you have any window decorations (status bar, navigation bar, title bar, etc.) that the dialog overlaps with when it's shown, you can provide a `LocalWindowDecorations` value with the insets for the dialog to avoid.

You can also pass a `DpRect` value directly to a specific dialog with the `windowDecorations` argument.

Note that while `DpRect` uses absolute left/right values, they are used as relative start/end values.

```kotlin
CompositionLocalProvider(
    LocalWindowDecorations provides DpRect(
        left = /* ... */,
        top = /* ... */,
        right = /* ... */,
        bottom = /* ... */,
    ),
) {
    InWindowAlertDialog(
        // ...
    )
}
```
