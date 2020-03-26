package com.hnrhn.downpour.impl.deluge.jsonobjects

import kotlinx.serialization.Serializable

@Serializable
class GetFreeSpaceResponse(
    val id: Int,
    val result: Long?,
    val error: ResponseError?
)
