package com.hnrhn.downpour.impl.deluge.jsonobjects

import com.hnrhn.downpour.common.FileInfo
import kotlinx.serialization.Serializable

@Serializable
data class FileInTorrent(
    val index: Int,
    val path: String,
    val offset: Long,
    val size: Long
) {
    fun toFileInfo(): FileInfo = FileInfo(
        path.split("/").last(),
        size
    )
}