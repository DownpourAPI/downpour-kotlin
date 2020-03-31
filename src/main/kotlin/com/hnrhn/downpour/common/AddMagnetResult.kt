package com.hnrhn.downpour.common

class AddMagnetResult (
    val status: AddMagnetStatus,
    val resultHash: String? = null
) {
    companion object {
        fun success(hash: String): AddMagnetResult {
            return AddMagnetResult(
                AddMagnetStatus.Success,
                hash
            )
        }

        fun alreadyExists(): AddMagnetResult {
            return AddMagnetResult(AddMagnetStatus.AlreadyExists)
        }

        fun failure(): AddMagnetResult {
            return AddMagnetResult(AddMagnetStatus.Failure)
        }
    }
}