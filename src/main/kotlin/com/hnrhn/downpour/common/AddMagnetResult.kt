package com.hnrhn.downpour.impl.deluge.jsonobjects

class AddMagnetResult (
    val status: AddMagnetStatus,
    val resultHash: String? = null
) {
    companion object {
        fun Success(hash: String): AddMagnetResult {
            return AddMagnetResult(AddMagnetStatus.Success, hash)
        }

        fun AlreadyExists(): AddMagnetResult {
            return AddMagnetResult(AddMagnetStatus.AlreadyExists)
        }

        fun Failure(): AddMagnetResult {
            return AddMagnetResult(AddMagnetStatus.Failure)
        }
    }
}