/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package com.hnrhn.downpour.common

data class Torrent (
    val hash: String,
    val name: String,
    val uploadRate: Long,
    val downloadRate: Long,
    val numberOfSeeds: Int,
    val numberOfPeers: Int,
    val ratio: Double,
    val totalSizeBytes: Long,
    val progress: Double,
    val state: String,
    val timeAdded: Double,
    val totalDownloaded: Long,
    val totalUploaded: Long,
    val files: List<FileInfo>,
    val remotePath: String
)