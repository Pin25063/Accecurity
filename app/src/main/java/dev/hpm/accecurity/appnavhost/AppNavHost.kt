package dev.hpm.accecurity.appnavhost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import dev.hpm.accecurity.model.TipoUsuario
import dev.hpm.accecurity.ui.screens.ActivityScreen
import dev.hpm.accecurity.ui.screens.FormularioLogIn
import dev.hpm.accecurity.ui.screens.HomeScreenAdmin
import dev.hpm.accecurity.ui.screens.PasesScreenAdminYResidente
import dev.hpm.accecurity.ui.screens.PerfilScreen
import kotlinx.serialization.Serializable

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

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginDestination,
        modifier = modifier
    ) {
        composable<LoginDestination> {
            FormularioLogIn(
                onLoginSuccess = {
                    navController.navigate(AdminGraph) {
                        popUpTo<LoginDestination> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        navigation<AdminGraph>(startDestination = AdminInicioDestination) {
            composable<AdminInicioDestination> {
                HomeScreenAdmin(
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
                    tipoUsuario = TipoUsuario.ADMIN,
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
                    tipoUsuario = TipoUsuario.ADMIN,
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
                    onLogOutClick = {
                        navController.navigate(LoginDestination) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}
