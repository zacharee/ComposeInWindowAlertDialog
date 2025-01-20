import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.zwander.compose.alertdialog.InWindowAlertDialog

fun main() {
    System.setProperty("apple.laf.useScreenMenuBar", "true")
    System.setProperty("apple.awt.application.appearance", "system")

    application {
        val windowState = rememberWindowState(
            size = DpSize(900.dp, 600.dp),
        )

        Window(
            onCloseRequest = ::exitApplication,
            title = "Sample",
            state = windowState,
        ) {
            var showingDialog by remember {
                mutableStateOf(false)
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                Box {
                    OutlinedButton(
                        onClick = {
                            showingDialog = true
                        },
                    ) {
                        Text(text = "Open Dialog")
                    }
                }

                InWindowAlertDialog(
                    showing = showingDialog,
                    title = { Text(text = "Dialog Title") },
                    text = {
                        for (i in 0 until 50) {
                            Text(text = "Line $i")
                        }
                    },
                    buttons = {
                        TextButton(
                            onClick = { showingDialog = false },
                        ) {
                            Text(text = "Close")
                        }
                    },
                    onDismissRequest = { showingDialog = false }
                )
            }
        }
    }
}