package com.frontend.nutricheck.client.ui.view_model.ai_handling

import android.net.Uri
import okhttp3.MultipartBody

/**
 * Interface for processing images captured from camera for AI meal analysis.
 *
 * Provides functionality to process images by correcting orientation based on EXIF data
 * and converting them to the appropriate format for API transmission.
 */
interface ImageProcessor {

    /**
     * Processes an image by applying EXIF rotation correction and converting to PNG format.
     *
     * @param uri The URI of the original image to process
     * @return The URI of the processed image, or null if processing fails
     */
    fun processImageWithRotation(uri: Uri): Uri?

    /**
     * Converts an image URI to a MultipartBody.Part for HTTP multipart upload.
     *
     * @param uri The URI of the image to convert, can be null
     * @return MultipartBody.Part ready for API transmission, or null if conversion fails
     */
    fun convertUriToMultipartBody(uri: Uri?): MultipartBody.Part?
}