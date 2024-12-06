package rs.xor.rencfs.krencfs.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rs.xor.rencfs.krencfs.data.sqldelight.SQLDelightDB
import rs.xor.rencfs.krencfs.data.vault.VaultModel
import rs.xor.rencfs.krencfs.ui.components.VaultEditor

@Composable
fun VaultDetailScreen(
    vaultId: String?,
    isEditing: Boolean,
    onSave: (VaultModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var vault by remember { mutableStateOf<VaultModel?>(null) }

    LaunchedEffect(vaultId) {
        vaultId?.let {
            SQLDelightDB.getVaultRepositoryAsync()
                .getVault(it.toLong())?.let { vaultModel ->
                    vault = vaultModel
                }
        }
    }

    vault?.let { vaultModel ->
        if (isEditing) {
            VaultEditor(
                vault = vaultModel,
                onSave = onSave,
                modifier = modifier
            )
        } else {
            VaultViewer(
                vault = vaultModel,
                modifier = modifier
            )
        }
    } ?: Text("Vault not found")
}

@Composable
private fun VaultViewer(
    vault: VaultModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Name",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = vault.name,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Mount Point",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = vault.mountPoint,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Data Directory",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = vault.dataDir,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}