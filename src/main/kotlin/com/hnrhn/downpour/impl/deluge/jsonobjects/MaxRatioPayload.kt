package com.hnrhn.downpour.impl.deluge.jsonobjects

class MaxRatioPayload(private val torrentHash: String, private val ratio: Int) {
    override fun toString(): String {
        return """{"id":1,"method":"core.set_torrent_options","params":[["$torrentHash"],{"stop_at_ratio":true,"stop_ratio":$ratio}]}"""
    }
}