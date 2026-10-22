package dev.hpm.accecurity.core.auth

import com.google.firebase.firestore.FirebaseFirestore
import dev.hpm.accecurity.core.model.TipoUsuario
import kotlinx.coroutines.tasks.await

/**
 * Obtiene el rol asignado a un usuario desde Firestore.
 *
 * Estructura esperada:
 * - Colección: `users`
 * - Documento ID: UID de Firebase Auth (mismo que en Authentication → Usuarios)
 * - Campo: `rol` con valores `admin`, `residente`, `jefe` o `guardia`
 */
class UserRoleRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun fetchAssignedRole(uid: String): AssignedRoleResult {
        return try {
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()

            if (!snapshot.exists()) {
                return AssignedRoleResult.MissingDocument
            }

            val role = snapshot.getString(ROLE_FIELD)
                ?.lowercase()
                ?.trim()

            val tipoUsuario = parseAssignedRole(role)
            if (tipoUsuario == null) {
                AssignedRoleResult.InvalidRole
            } else {
                AssignedRoleResult.Success(tipoUsuario)
            }
        } catch (_: Exception) {
            AssignedRoleResult.NetworkOrServerError
        }
    }

    companion object {
        const val USERS_COLLECTION = "users"
        const val ROLE_FIELD = "rol"
    }
}

sealed interface AssignedRoleResult {
    data class Success(val tipoUsuario: TipoUsuario) : AssignedRoleResult
    data object MissingDocument : AssignedRoleResult
    data object InvalidRole : AssignedRoleResult
    data object NetworkOrServerError : AssignedRoleResult
}
