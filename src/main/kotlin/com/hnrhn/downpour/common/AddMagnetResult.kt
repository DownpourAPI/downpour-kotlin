/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

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