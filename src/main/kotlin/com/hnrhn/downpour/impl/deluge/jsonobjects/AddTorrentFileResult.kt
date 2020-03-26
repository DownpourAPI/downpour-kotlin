package com.hnrhn.downpour.impl.deluge.jsonobjects

class AddTorrentFileResult (val status: AddTorrentFileStatus, val resultHash: String? = null) {
    companion object {
        fun Success(hash: String): AddTorrentFileResult {
            return AddTorrentFileResult(AddTorrentFileStatus.Success, hash)
        }

        fun AlreadyExists(): AddTorrentFileResult {
            return AddTorrentFileResult(AddTorrentFileStatus.AlreadyExists)
        }

        fun Failure(): AddTorrentFileResult {
            return AddTorrentFileResult(AddTorrentFileStatus.Failure)
        }
    }
}