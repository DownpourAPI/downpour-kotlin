/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

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