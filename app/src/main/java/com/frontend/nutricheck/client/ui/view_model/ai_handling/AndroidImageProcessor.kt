package com.frontend.nutricheck.client.ui.view_model.ai_handling

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 *  Implementation of [ImageProcessor], providing methods to process images,
 *  including handling EXIF rotation
 */
@Singleton
class AndroidImageProcessor @Inject constructor(
    @ApplicationContext private val appContext: Context
) : ImageProcessor {

    companion object {
        private const val MIME_TYPE_JPEG = "image/jpeg"
    }

    /**
     * Converts a URI to MultipartBody.Part for API transmission.
     * Handles image processing including format conversion and rotation correction.
     *
     * @param uri The image URI to convert
     * @return MultipartBody.Part or null if conversion fails
     */
    override fun convertUriToMultipartBody(uri: Uri?): MultipartBody.Part? {
        if (uri == null) return null
        return try {
            val contentResolver = appContext.contentResolver
            val mimeType = contentResolver.getType(uri) ?: MIME_TYPE_JPEG

            val processedUri = processImageWithRotation(uri) ?: uri

            createMultipartBodyPart(processedUri, mimeType, contentResolver)
        } catch (e: Exception) {
            Log.e("AndroidImageProcessor", "Error converting URI to MultipartBody: $uri", e)
            null
        }
    }


    /**
     * Processes JPEG images by applying EXIF rotation and converting to PNG.
     * This ensures consistent image orientation regardless of device rotation.
     *
     * @param uri The original image URI
     * @return Processed image URI or null if processing fails
     */
    override fun processImageWithRotation(uri: Uri): Uri? {
        return try {
            val contentResolver = appContext.contentResolver

            // Load the bitmap from URI
            val originalBitmap = contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: return null

            // Get EXIF orientation from the image
            val orientation = contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL

            // Apply rotation if needed
            val rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(originalBitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(originalBitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(originalBitmap, 270f)
                else -> originalBitmap
            }

            val jpegUri = saveBitmapAsJpeg(rotatedBitmap, contentResolver)

            // Clean up memory
            if (rotatedBitmap != originalBitmap) {
                originalBitmap.recycle()
            }
            rotatedBitmap.recycle()

            jpegUri
        } catch (e: Exception) {
            Log.e("AndroidImageProcessor", "Error processing image with rotation: $uri", e)
            null
        }
    }
    /**
     * Rotates a bitmap by the specified degrees.
     */
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    /**
     * Saves a bitmap as PNG to MediaStore.
     * @param bitmap The bitmap to save
     * @param contentResolver ContentResolver for access
     * @return URI of saved image or null if failed
     */
    private fun saveBitmapAsJpeg(bitmap: Bitmap, contentResolver: ContentResolver): Uri? {
        val name = "${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE_JPEG)
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/NutriCheck")
        }

        return contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )?.also { jpegUri ->
            contentResolver.openOutputStream(jpegUri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
    }
    /**
     * Creates a MultipartBody.Part from URI and MIME type for API transmission.
     * @param uri The image URI
     * @param mimeType The MIME type of the image
     * @param contentResolver ContentResolver for accessing the image
     * @return MultipartBody.Part or null if creation fails
     */
    private fun createMultipartBodyPart(
        uri: Uri,
        mimeType: String,
        contentResolver: ContentResolver
    ): MultipartBody.Part? {
        val partName = "file"
        val fileName = getFileNameFromUri(uri, contentResolver)
            ?: "upload.jpg" // Fallback name if not available

        // Creates a RequestBody from URI for multipart upload
        val requestBody = object : RequestBody() {
            override fun contentType() = mimeType.toMediaTypeOrNull()
                ?: "application/octet-stream".toMediaType()

            override fun contentLength(): Long =
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (sizeIndex != -1) cursor.getLong(sizeIndex) else -1
                    } else -1
                } ?: -1

            override fun writeTo(sink: BufferedSink) {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.copyTo(sink.outputStream())
                } ?: throw IOException("Failed to open InputStream for URI: $uri")
            }
        }

        return MultipartBody.Part.createFormData(partName, fileName, requestBody)
    }
    /**
     * Gets the display name of a file from its URI.
     * @param uri The file URI
     * @param contentResolver ContentResolver for access
     * @return File name or null if not available
    */
    private fun getFileNameFromUri(uri: Uri, contentResolver: ContentResolver): String? {
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) cursor.getString(nameIndex) else null
            } else null
        }
    }
}
