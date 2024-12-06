package rs.xor.rencfs.krencfs.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.compose.rememberDirectoryPickerLauncher
import rs.xor.rencfs.krencfs.data.vault.VaultModel

@Composable
fun VaultEditor(
    vault: VaultModel,
    onSave: (VaultModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var editedVault by remember(vault) { mutableStateOf(vault) }
    var showSaveConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        VaultFormField(
            label = "Name",
            value = editedVault.name,
            onValueChange = { editedVault = editedVault.copy(name = it) }
        )

        VaultFormField(
            label = "Mount Point",
            value = editedVault.mountPoint,
            onValueChange = { editedVault = editedVault.copy(mountPoint = it) }
        )

        DataDirField(
            value = editedVault.dataDir,
            onValueChange = { editedVault = editedVault.copy(dataDir = it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSave(editedVault)
                showSaveConfirmation = true
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }

        if (showSaveConfirmation) {
            AlertDialog(
                onDismissRequest = { showSaveConfirmation = false },
                title = { Text("Success") },
                text = { Text("Vault settings saved successfully") },
                confirmButton = {
                    TextButton(onClick = { showSaveConfirmation = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VaultFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DataDirField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Data Directory") },
            modifier = Modifier.weight(1f)
        )

        val launcher = rememberDirectoryPickerLauncher(
            title = "Choose data folder",
            initialDirectory = value
        ) { directory ->
            directory?.path?.let(onValueChange)
        }

        IconButton(
            onClick = { launcher.launch() },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(Icons.Outlined.MoreHoriz, "Browse")
        }
    }
}