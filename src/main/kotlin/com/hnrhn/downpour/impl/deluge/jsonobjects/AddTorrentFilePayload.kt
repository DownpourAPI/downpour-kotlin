package com.hnrhn.downpour.impl.deluge.jsonobjects

import java.io.File
import java.util.*

class AddTorrentFilePayload(torrentFile: File) {
    private val fileDump: String = Base64.getEncoder().encodeToString(torrentFile.readBytes())

    override fun toString(): String {
        return """{"id":1, "method":"core.add_torrent_file", "params":["file","$fileDump",{}]}"""
    }
}