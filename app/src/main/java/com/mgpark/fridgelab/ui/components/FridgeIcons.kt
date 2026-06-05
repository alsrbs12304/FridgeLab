package com.mgpark.fridgelab.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.ui.graphics.vector.ImageVector

/** README §8 아이콘 매핑 → Material Symbols. */
object FridgeIcons {
    val back: ImageVector = Icons.AutoMirrored.Filled.ArrowBackIos
    val close: ImageVector = Icons.Filled.Close
    val chevron: ImageVector = Icons.Filled.ChevronRight
    val camera: ImageVector = Icons.Filled.PhotoCamera
    val flashOn: ImageVector = Icons.Filled.FlashOn
    val flashOff: ImageVector = Icons.Filled.FlashOff
    val gallery: ImageVector = Icons.Filled.Image
    val sparkle: ImageVector = Icons.Filled.AutoAwesome
    val check: ImageVector = Icons.Filled.Check
    val add: ImageVector = Icons.Filled.Add
    val remove: ImageVector = Icons.Filled.Remove
    val trash: ImageVector = Icons.Outlined.Delete
    val clock: ImageVector = Icons.Filled.Schedule
    val flame: ImageVector = Icons.Filled.LocalFireDepartment
    val users: ImageVector = Icons.Filled.Group
    val search: ImageVector = Icons.Filled.Search
    val bookmarkBorder: ImageVector = Icons.Outlined.Bookmark
    val bookmark: ImageVector = Icons.Filled.Bookmark
    val refresh: ImageVector = Icons.Filled.Cached
    val edit: ImageVector = Icons.Outlined.Edit
    val scan: ImageVector = Icons.Filled.CropFree
    val leaf: ImageVector = Icons.Filled.Eco
    val flip: ImageVector = Icons.Filled.Cameraswitch
}
