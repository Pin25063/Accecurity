package dev.hpm.accecurity.core.model

data class AccessSyncStatus(
    val online: Boolean = false,
    val pending: Int = 0,
    val conflicts: Int = 0,
    val syncing: Boolean = false,
    val issues: List<String> = emptyList()
)
