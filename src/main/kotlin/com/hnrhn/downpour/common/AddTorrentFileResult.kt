/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package com.hnrhn.downpour.common

class AddTorrentFileResult (val status: AddTorrentFileStatus, val resultHash: String? = null) {
    companion object {
        fun success(hash: String): AddTorrentFileResult {
            return AddTorrentFileResult(
                AddTorrentFileStatus.Success,
                hash
            )
        }

        fun alreadyExists(): AddTorrentFileResult {
            return AddTorrentFileResult(AddTorrentFileStatus.AlreadyExists)
        }

        fun failure(): AddTorrentFileResult {
            return AddTorrentFileResult(AddTorrentFileStatus.Failure)
        }
    }
}