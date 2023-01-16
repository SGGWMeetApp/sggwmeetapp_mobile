package pl.sggw.sggwmeet.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ImageSupportUtil {
    companion object {

        fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
            try {
                Log.i(
                    "SGGWMA " + this::class.simpleName,
                    "Rozmiar przed: " + source.width.toString() + "x" + source.height.toString()
                )
                var output: Bitmap
                if (source.height >= source.width) {
                    val aspectRatio = source.width.toDouble() / source.height.toDouble()
                    val targetWidth = (maxLength * aspectRatio).toInt()
                    output = Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
                } else {
                    val aspectRatio = source.height.toDouble() / source.width.toDouble()
                    val targetHeight = (maxLength * aspectRatio).toInt()
                    output = Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
                }
                Log.i(
                    "resizeBitmap " + this::class.simpleName,
                    "Rozmiar po: " + output.width.toString() + "x" + output.height.toString()
                )
                return output
            } catch (e: Exception) {
                return source
            }
        }

        //Brak kompresji
        fun bitmapToPng(bitmap: Bitmap?, fileNameToSave: String, context: Context): File? {
            return bitmapToFile(bitmap, fileNameToSave, context, CompressFormat.PNG, 100)
        }

        //Z kompresją, opcjonalnie można zadeklarować jakość obrazu.
        //Im mniejsza wartość jakości, tym większy stopień konwersji.
        fun bitmapToJpeg(
            bitmap: Bitmap?,
            fileNameToSave: String,
            context: Context,
            quality: Int = 90
        ): File? {
            return bitmapToFile(bitmap, fileNameToSave, context, CompressFormat.JPEG, quality)
        }

        private fun bitmapToFile(
            image: Bitmap?, fileNameToSave: String, context: Context,
            compressFormat: CompressFormat, quality: Int
        ): File? {
            var file: File? = null
            return try {
                file = File(
                    context.cacheDir.path + File.separator + fileNameToSave +
                            "." + compressFormat.name
                )
                if (!file.exists()) {
                    file.createNewFile()
                }

                val byteArrayOutput = ByteArrayOutputStream()
                image!!.compress(compressFormat, quality, byteArrayOutput)
                val byteArray = byteArrayOutput.toByteArray()

                val fileOutputStream = FileOutputStream(file)
                fileOutputStream.write(byteArray)
                fileOutputStream.flush()
                fileOutputStream.close()
                file
            } catch (e: Exception) {
                e.printStackTrace()
                file //  return null
            }
        }
    }
}