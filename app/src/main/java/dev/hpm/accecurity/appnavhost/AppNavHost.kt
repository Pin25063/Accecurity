package dev.hpm.accecurity.appnavhost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import dev.hpm.accecurity.core.auth.parseAssignedRole
import dev.hpm.accecurity.core.auth.resolveUserDisplay
import dev.hpm.accecurity.core.data.AppGraph
import dev.hpm.accecurity.core.model.TipoUsuario
import dev.hpm.accecurity.feature.activity.ActivityScreen
import dev.hpm.accecurity.feature.inside.ActualmenteAdentroScreen
import dev.hpm.accecurity.feature.escaner.EscanerScreenGuardia
import dev.hpm.accecurity.feature.login.FormularioLogIn
import dev.hpm.accecurity.feature.pases.HomeScreenAdmin
import dev.hpm.accecurity.feature.pases.PasesScreenAdminYResidente
import dev.hpm.accecurity.feature.profile.PerfilScreen
import dev.hpm.accecurity.feature.solicitudes.SolicitudesScreenJefe
import kotlinx.serialization.Serializable
import dev.hpm.accecurity.feature.registro.RegistroGaritaScreen

@Serializable
object LoginDestination

@Serializable
object AdminGraph

@Serializable
object AdminInicioDestination

@Serializable
object AdminPasesDestination

@Serializable
object AdminActividadDestination

@Serializable
object AdminPerfilDestination

@Serializable
object JefeGraph

@Serializable
object JefeSolicitudesDestination

@Serializable
object JefeAdentroDestination

@Serializable
object JefeActividadDestination

@Serializable
object JefePerfilDestination

@Serializable
object GuardiaGraph

@Serializable
object GuardiaEscanerDestination

@Serializable
object GuardiaRegistroDestination

@Serializable
object GuardiaPerfilDestination

private fun NavHostController.navigateToLoginAfterSignOut() {
    navigate(LoginDestination) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val currentUserEntity by remember { AppGraph.repository.observeCurrentUser() }
        .collectAsStateWithLifecycle(initialValue = null)
    val pendingRequests by remember { AppGraph.repository.observePending() }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val numeroSolicitudesJefe = pendingRequests.size

    var adminTipoUsuario by rememberSaveable { mutableStateOf(TipoUsuario.ADMIN) }
    val tipoUsuarioAdminActual = parseAssignedRole(currentUserEntity?.role)
        ?.takeIf { it == TipoUsuario.ADMIN || it == TipoUsuario.RESIDENTE }
        ?: adminTipoUsuario

    val firebaseUser = firebaseAuth.currentUser
    val userDisplay = resolveUserDisplay(
        displayName = firebaseUser?.displayName,
        email = currentUserEntity?.email?.takeIf { it.isNotBlank() } ?: firebaseUser?.email
    )

    NavHost(
        navController = navController,
        startDestination = LoginDestination,
        modifier = modifier
    ) {
        composable<LoginDestination> {
            FormularioLogIn(
                onLoginSuccess = { session ->
                    when (session.tipoUsuario) {
                        TipoUsuario.ADMIN, TipoUsuario.RESIDENTE -> {
                            adminTipoUsuario = session.tipoUsuario
                            navController.navigate(AdminGraph) {
                                popUpTo<LoginDestination> { inclusive = true }
                            }
                        }
                        TipoUsuario.JEFE -> {
                            navController.navigate(JefeGraph) {
                                popUpTo<LoginDestination> { inclusive = true }
                            }
                        }
                        TipoUsuario.GUARDIA -> {
                            navController.navigate(GuardiaGraph) {
                                popUpTo<LoginDestination> { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        navigation<AdminGraph>(startDestination = AdminInicioDestination) {
            composable<AdminInicioDestination> {
                HomeScreenAdmin(
                    nombreUsuario = userDisplay.firstName,
                    onInicioClick = {
                        navController.navigate(AdminInicioDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPasesClick = {
                        navController.navigate(AdminPasesDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onActividadClick = {
                        navController.navigate(AdminActividadDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPerfilClick = {
                        navController.navigate(AdminPerfilDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable<AdminPasesDestination> {
                PasesScreenAdminYResidente(
                    onInicioClick = {
                        navController.navigate(AdminInicioDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPasesClick = {
                        navController.navigate(AdminPasesDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onActividadClick = {
                        navController.navigate(AdminActividadDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPerfilClick = {
                        navController.navigate(AdminPerfilDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable<AdminActividadDestination> {
                ActivityScreen(
                    tipoUsuario = tipoUsuarioAdminActual,
                    onInicioClick = {
                        navController.navigate(AdminInicioDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPasesClick = {
                        navController.navigate(AdminPasesDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onActividadClick = {
                        navController.navigate(AdminActividadDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPerfilClick = {
                        navController.navigate(AdminPerfilDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable<AdminPerfilDestination> {
                PerfilScreen(
                    nombre = userDisplay.fullName,
                    iniciales = userDisplay.initials,
                    tipoUsuario = tipoUsuarioAdminActual,
                    onInicioClick = {
                        navController.navigate(AdminInicioDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPasesClick = {
                        navController.navigate(AdminPasesDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onActividadClick = {
                        navController.navigate(AdminActividadDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPerfilClick = {
                        navController.navigate(AdminPerfilDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogOutClick = { navController.navigateToLoginAfterSignOut() }
                )
            }
        }

        navigation<JefeGraph>(startDestination = JefeSolicitudesDestination) {
            composable<JefeSolicitudesDestination> {
                SolicitudesScreenJefe(
                    onSolicitudesClick = {
                        navController.navigate(JefeSolicitudesDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onAdentroClick = {
                        navController.navigate(JefeAdentroDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onActividadClick = {
                        navController.navigate(JefeActividadDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPerfilClick = {
                        navController.navigate(JefePerfilDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable<JefeAdentroDestination> {
                ActualmenteAdentroScreen(
                    numeroSolicitudesJefe = numeroSolicitudesJefe,
                    onSolicitudesClick = {
                        navController.navigate(JefeSolicitudesDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onAdentroClick = {
                        navController.navigate(JefeAdentroDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onActividadClick = {
                        navController.navigate(JefeActividadDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPerfilClick = {
                        navController.navigate(JefePerfilDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable<JefeActividadDestination> {
                ActivityScreen(
                    tipoUsuario = TipoUsuario.JEFE,
                    numeroSolicitudesJefe = numeroSolicitudesJefe,
                    onSolicitudesClick = {
                        navController.navigate(JefeSolicitudesDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onAdentroClick = {
                        navController.navigate(JefeAdentroDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onActividadClick = {
                        navController.navigate(JefeActividadDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPerfilClick = {
                        navController.navigate(JefePerfilDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable<JefePerfilDestination> {
                PerfilScreen(
                    nombre = userDisplay.fullName,
                    iniciales = userDisplay.initials,
                    tipoUsuario = TipoUsuario.JEFE,
                    numeroSolicitudesJefe = numeroSolicitudesJefe,
                    onSolicitudesClick = {
                        navController.navigate(JefeSolicitudesDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onAdentroClick = {
                        navController.navigate(JefeAdentroDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onActividadClick = {
                        navController.navigate(JefeActividadDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPerfilClick = {
                        navController.navigate(JefePerfilDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogOutClick = { navController.navigateToLoginAfterSignOut() }
                )
            }
        }

        navigation<GuardiaGraph>(startDestination = GuardiaEscanerDestination) {
            composable<GuardiaEscanerDestination> {
                EscanerScreenGuardia(
                    onEscanerClick = {
                        navController.navigate(GuardiaEscanerDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onRegistroClick = {
                        navController.navigate(GuardiaRegistroDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPerfilClick = {
                        navController.navigate(GuardiaPerfilDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable<GuardiaRegistroDestination> {
                RegistroGaritaScreen(
                    onEscanerClick = {
                        navController.navigate(GuardiaEscanerDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onRegistroClick = {
                        navController.navigate(GuardiaRegistroDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPerfilClick = {
                        navController.navigate(GuardiaPerfilDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable<GuardiaPerfilDestination> {
                PerfilScreen(
                    nombre = userDisplay.fullName,
                    iniciales = userDisplay.initials,
                    tipoUsuario = TipoUsuario.GUARDIA,
                    onEscanerClick = {
                        navController.navigate(GuardiaEscanerDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onRegistroClick = {
                        navController.navigate(GuardiaRegistroDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPerfilClick = {
                        navController.navigate(GuardiaPerfilDestination) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogOutClick = { navController.navigateToLoginAfterSignOut() }
                )
            }
        }
    }
}
