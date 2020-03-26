package com.hnrhn.downpour.impl.deluge.jsonobjects

class PauseTorrentPayload(private val torrentHash: String) {
    override fun toString(): String {
        return """{"id":1,"method":"core.pause_torrent","params":["$torrentHash"]}"""
    }
}