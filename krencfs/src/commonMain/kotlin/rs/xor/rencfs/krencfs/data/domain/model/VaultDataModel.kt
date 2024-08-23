package rs.xor.rencfs.krencfs.data.domain.model

import rs.xor.rencfs.krencfs.Vaults

data class VaultDataModel(
    val id: Long? = null,
    val name: String,
    val mountPoint: String,
    val dataDir: String,
)

fun Vaults.toVaultDataModel(): VaultDataModel {
    return VaultDataModel(
        id = this.id,
        name = this.name,
        mountPoint = this.mount,
        dataDir = this.path,
    )
}
