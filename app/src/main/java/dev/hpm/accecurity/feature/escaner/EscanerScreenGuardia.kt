package dev.hpm.accecurity.feature.escaner

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dev.hpm.accecurity.core.ui.components.escaner.ResultadoEscaneoDialog
import dev.hpm.accecurity.core.ui.components.escaner.BadgeEnLinea
import dev.hpm.accecurity.core.ui.components.escaner.MarcoEscanerOverlay
import dev.hpm.accecurity.core.ui.components.escaner.VisorQrPlaceholder
import dev.hpm.accecurity.core.ui.components.navigation.BottomNavigationBarGuardia
import java.util.concurrent.Executors

/**
 * Pantalla de escáner QR para el rol guardia de seguridad.
 * Implementa captura real con CameraX y detección con Google ML Kit Barcode Scanning bajo arquitectura MVI.
 */
@Composable
fun EscanerScreenGuardia(
    modifier: Modifier = Modifier,
    onEscanerClick: () -> Unit = {},
    onRegistroClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onRegistrarSalidaClick: () -> Unit = {},
    viewModel: EscanerViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isInspectionMode = LocalInspectionMode.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Control de permisos de cámara en tiempo de ejecución
    var tienePermisoCamara by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        tienePermisoCamara = concedido
        viewModel.onEvent(EscanerEvent.PermisoCamaraActualizado(concedido))
        if (!concedido) {
            viewModel.onEvent(
                EscanerEvent.ErrorOcurrido("Permiso de cámara denegado. Se requiere para escanear pases QR.")
            )
        }
    }

    LaunchedEffect(Unit) {
        if (!tienePermisoCamara) {
            permisoLauncher.launch(Manifest.permission.CAMERA)
        } else {
            viewModel.onEvent(EscanerEvent.PermisoCamaraActualizado(true))
        }
    }

    // Manejo de mensajes de error y éxito mediante Snackbar y Toast
    LaunchedEffect(state.mensajeError) {
        state.mensajeError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.onEvent(EscanerEvent.LimpiarMensajes)
        }
    }

    LaunchedEffect(state.mensajeExito) {
        state.mensajeExito?.let { exito ->
            Toast.makeText(context, exito, Toast.LENGTH_SHORT).show()
            snackbarHostState.showSnackbar(
                message = exito,
                duration = SnackbarDuration.Short
            )
            viewModel.onEvent(EscanerEvent.LimpiarMensajes)
        }
    }

    // Componentes para CameraX y ML Kit
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        BarcodeScanning.getClient(options)
    }

    DisposableEffect(Unit) {
        onDispose {
            analysisExecutor.shutdown()
            barcodeScanner.close()
        }
    }

    val currentEstaEscaneando by rememberUpdatedState(state.estaEscaneando)
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    // Inicialización y enlace del ciclo de vida de CameraX
    LaunchedEffect(tienePermisoCamara, lifecycleOwner) {
        if (!tienePermisoCamara || isInspectionMode) return@LaunchedEffect

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
                    if (!currentEstaEscaneando) {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    @OptIn(ExperimentalGetImage::class)
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val inputImage = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )
                        barcodeScanner.process(inputImage)
                            .addOnSuccessListener { barcodes ->
                                val qrValido = barcodes.firstOrNull { !it.rawValue.isNullOrBlank() }?.rawValue
                                if (qrValido != null) {
                                    viewModel.onEvent(EscanerEvent.CodigoEscaneado(qrValido))
                                }
                            }
                            .addOnFailureListener {
                                viewModel.onEvent(EscanerEvent.ErrorOcurrido(
                                    "No se pudo leer el código. Tocá Escanear código QR para reintentar."
                                ))
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                val cameraSelector = if (provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }

                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                viewModel.onEvent(
                    EscanerEvent.ErrorOcurrido("Error al inicializar la cámara: ${e.localizedMessage ?: "Fallo desconocido"}")
                )
            }
        }, ContextCompat.getMainExecutor(context))
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    if (state.mostrarResultado) {
        ResultadoEscaneoDialog(
            pase = state.paseValidado,
            consultando = state.consultaEnCurso,
            registrando = state.registroEnCurso,
            mensaje = state.mensajeResultado,
            entradaRegistrada = state.entradaRegistrada,
            onConfirmar = { viewModel.onEvent(EscanerEvent.ConfirmarEntrada) },
            onCerrar = { viewModel.onEvent(EscanerEvent.CerrarResultado) }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Escáner de garita",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Garita Principal • Estación del guardia",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BadgeEnLinea()
            }
        },
        bottomBar = {
            BottomNavigationBarGuardia(
                escaner = true,
                registro = false,
                perfil = false,
                onEscanerClick = onEscanerClick,
                onRegistroClick = onRegistroClick,
                onPerfilClick = onPerfilClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Visor de Cámara Real con superposición de marco guía
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF232628)),
                contentAlignment = Alignment.Center
            ) {
                if (isInspectionMode) {
                    VisorQrPlaceholder(modifier = Modifier.fillMaxSize())
                } else if (!tienePermisoCamara) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = "Permiso de cámara",
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Se requiere acceso a la cámara para escanear los códigos QR de los visitantes.",
                            color = Color.White,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { permisoLauncher.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(text = "Conceder permiso")
                        }
                    }
                } else {
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier.fillMaxSize()
                    )
                    MarcoEscanerOverlay(
                        modifier = Modifier.fillMaxSize(),
                        ultimoCodigo = state.ultimoCodigoEscaneado
                    )
                }
            }

            // Botones inferiores de acción
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    enabled = !state.consultaEnCurso && !state.registroEnCurso && !state.mostrarResultado,
                    onClick = {
                        if (!tienePermisoCamara) {
                            permisoLauncher.launch(Manifest.permission.CAMERA)
                        } else if (cameraProvider == null) {
                            viewModel.onEvent(
                                EscanerEvent.ErrorOcurrido("La cámara aún se está inicializando o no está disponible.")
                            )
                        } else {
                            viewModel.onEvent(EscanerEvent.ReiniciarEscaner)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = "Escanear código QR",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (!state.estaEscaneando && state.ultimoCodigoEscaneado != null) {
                            "Escanear otro código"
                        } else {
                            "Escanear código QR"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = onRegistrarSalidaClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                        contentDescription = "Registrar salida",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Registrar salida",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
