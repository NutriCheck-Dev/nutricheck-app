package com.frontend.nutricheck.client.model.data_sources.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.graphics.vector.ImageVector

enum class DropdownMenuOptions(
    private val option: String,
    private val icon: ImageVector
) {
    DOWNLOAD("Download", Icons.Default.Download),
    DELETE("Delete", Icons.Default.Delete),
    EDIT("Edit", Icons.Default.Edit),
    UPLOAD("Upload", Icons.Default.CloudUpload),
    REPORT("Report", Icons.Default.Report);

    override fun toString(): String {
        return option
    }

    fun getIcon(): ImageVector {
        return icon
    }
}