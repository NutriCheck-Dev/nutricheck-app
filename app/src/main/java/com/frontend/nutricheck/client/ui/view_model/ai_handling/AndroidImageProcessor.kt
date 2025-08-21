package com.frontend.nutricheck.client.ui.view_model.ai_handling

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for converting image URIs to multipart bodies for API uploads.
 * Handles image processing including rotation correction.
 */
@Singleton
class AndroidImageProcessor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Converts an image URI to a MultipartBody.Part for API upload.
     *
     * @param uri The URI of the image to convert
     * @return MultipartBody.Part containing the image data, or null if conversion fails
     */
     fun convertUriToMultipartBody(uri: Uri?): MultipartBody.Part? {
        return uri?.let {
            runCatching {
                val processedBytes = processImage(it) ?: return null
                val fileName = getFileName(it) ?: "image.jpg"
                val requestBody = processedBytes.toRequestBody("image/jpeg".toMediaType())
                MultipartBody.Part.createFormData("file", fileName, requestBody)
            }.onFailure {
                Log.e("ImageProcessor", "Error converting URI", it)
            }.getOrNull()
        }
    }
    /**
     * Processes an image from URI, applying rotation correction if needed.
     *
     * @param uri The URI of the image to process
     * @return Processed image as byte array, or null if processing fails
     */
    private fun processImage(uri: Uri): ByteArray? {
        return runCatching {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val rotation = getRotationFromExif(uri)
                // check if the image needs rotation, else read it directly
                if (rotation == 0f) {
                    inputStream.readBytes()
                } else {
                    val bitmap = BitmapFactory.decodeStream(inputStream) ?: return null
                    val rotatedBitmap = rotateBitmap(bitmap, rotation)
                    compressBitmapToBytes(bitmap, rotatedBitmap)
                }
            }
        }.onFailure {
            Log.e("ImageProcessor", "Error processing image", it)
        }.getOrNull()
    }
    /**
     * Extracts rotation angle from EXIF orientation data.
     *
     * @param uri The URI of the image to check
     * @return Rotation angle in degrees
     */
    private fun getRotationFromExif(uri: Uri): Float {
        return runCatching {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val orientation = ExifInterface(inputStream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
            } ?: 0f
        }.getOrElse { 0f }
    }
    /**
     * Rotates a bitmap by the specified angle using a transformation matrix.
     *
     * @param originalBitmap The bitmap to rotate
     * @param rotation The rotation angle in degrees
     * @return New rotated bitmap instance
     */
    private fun rotateBitmap(originalBitmap: Bitmap, rotation: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(rotation) }
        return Bitmap.createBitmap(
            originalBitmap, 0, 0,
            originalBitmap.width, originalBitmap.height,
            matrix, true
        )
    }
    /**
     * Compresses bitmap to JPEG format and returns as byte array.
     * Automatically handles memory cleanup by recycling bitmap resources.
     *
     * @param originalBitmap The original bitmap to recycle
     * @param rotatedBitmap The bitmap to compress (may be the same as originalBitmap)
     * @return Compressed image as byte array
     */
    private fun compressBitmapToBytes(originalBitmap: Bitmap, rotatedBitmap: Bitmap): ByteArray {
        return ByteArrayOutputStream().use { output ->
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
            originalBitmap.recycle()
            if (rotatedBitmap != originalBitmap) rotatedBitmap.recycle()
            output.toByteArray()
        }
    }
    /**
     * Gets the display name of a file from its URI.
     *
     * @param uri The file URI
     * @return File name or null if not available
     */
    private fun getFileName(uri: Uri): String? {
        return runCatching {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                cursor.takeIf { it.moveToFirst() }
                    ?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    ?.takeIf { it >= 0 }
                    ?.let { cursor.getString(it) }
            }
        }.getOrNull()
    }

}