package dev.hpm.accecurity.core.qr

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import dev.hpm.accecurity.core.model.PaseData
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Genera un QR real localmente, manteniendo el identificador guardado del pase. */
object PassQr {
    suspend fun generarImagen(codigo: String): Bitmap = withContext(Dispatchers.Default) {
        require(codigo.isNotBlank()) { "El pase no tiene código QR." }
        val matrix = QRCodeWriter().encode(
            codigo,
            BarcodeFormat.QR_CODE,
            768,
            768,
            mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.MARGIN to 4,
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M
            )
        )
        val pixels = IntArray(matrix.width * matrix.height) { index ->
            if (matrix[index % matrix.width, index / matrix.width]) Color.BLACK else Color.WHITE
        }
        Bitmap.createBitmap(pixels, matrix.width, matrix.height, Bitmap.Config.ARGB_8888)
    }

    /** Guarda una copia temporal y prepara el selector de aplicaciones para compartir. */
    suspend fun prepararCompartir(
        context: Context,
        imagen: Bitmap,
        pase: PaseData
    ): Intent = withContext(Dispatchers.IO) {
        val directory = File(context.cacheDir, "pases")
        if (!directory.isDirectory && !directory.mkdirs()) {
            throw IOException("No se pudo preparar la imagen del pase.")
        }
        val file = File.createTempFile("pase-", ".png", directory)
        file.outputStream().use { stream ->
            if (!imagen.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                throw IOException("No se pudo guardar la imagen del pase.")
            }
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.pases.fileprovider", file)
        val message = buildString {
            appendLine("Pase de acceso - Accecurity")
            appendLine("Visitante: ${pase.nombre}")
            if (pase.ubicacion.isNotBlank()) appendLine("Destino: ${pase.ubicacion}")
            if (pase.horarioAutorizado.isNotBlank()) appendLine("Ingreso: ${pase.horarioAutorizado}")
            append("Presentá este QR en garita dentro del horario autorizado.")
        }
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_SUBJECT, "Pase de acceso para ${pase.nombre}")
            clipData = ClipData.newRawUri("QR del pase", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        Intent.createChooser(sendIntent, "Compartir QR del pase").apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
