package dev.hpm.accecurity.core.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.tasks.await

/**
 * Autenticación y lectura del rol asignado.
 * La UI y la navegación no hablan con Firebase directamente.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val userRoleRepository: UserRoleRepository = UserRoleRepository()
) {
    suspend fun signIn(email: String, password: String): SignInResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val user = auth.currentUser
            val uid = user?.uid
            if (uid.isNullOrBlank()) {
                auth.signOut()
                return SignInResult.Failure("No se pudo obtener la sesión.")
            }

            when (val roleResult = userRoleRepository.fetchAssignedRole(uid)) {
                is AssignedRoleResult.Success -> SignInResult.Success(
                    AuthenticatedSession(
                        uid = uid,
                        email = user.email ?: email,
                        tipoUsuario = roleResult.tipoUsuario
                    )
                )
                AssignedRoleResult.MissingDocument -> {
                    auth.signOut()
                    SignInResult.Failure(
                        "Tu cuenta no tiene rol asignado. Crea el documento users/{uid} en Firestore."
                    )
                }
                AssignedRoleResult.InvalidRole -> {
                    auth.signOut()
                    SignInResult.Failure(
                        "Rol inválido en Firestore. Usa: admin, residente, jefe o guardia."
                    )
                }
                AssignedRoleResult.NetworkOrServerError -> {
                    auth.signOut()
                    SignInResult.Failure("No se pudo leer tu rol. Revisa internet o Firestore.")
                }
            }
        } catch (error: Exception) {
            SignInResult.Failure(mapAuthError(error))
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private fun mapAuthError(error: Exception): String {
        return when (error) {
            is FirebaseAuthInvalidUserException -> "No existe una cuenta con ese correo."
            is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos."
            else -> {
                val message = error.message.orEmpty().lowercase()
                if ("network" in message) {
                    "Sin conexión. Revisa tu internet."
                } else {
                    "No se pudo iniciar sesión. Intenta de nuevo."
                }
            }
        }
    }
}

sealed interface SignInResult {
    data class Success(val session: AuthenticatedSession) : SignInResult
    data class Failure(val message: String) : SignInResult
}
