package com.hnrhn.downpour.impl.deluge.jsonobjects

class ResumeTorrentPayload(private val torrentHash: String) {
    override fun toString(): String {
        return """{"id":1,"method":"core.resume_torrent","params":["$torrentHash"]}"""
    }
}