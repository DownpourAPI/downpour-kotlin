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