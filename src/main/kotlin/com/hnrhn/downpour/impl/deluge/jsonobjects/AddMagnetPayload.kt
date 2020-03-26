package com.hnrhn.downpour.impl.deluge.jsonobjects

class AddMagnetPayload(private val magnetLink: String) {
    override fun toString(): String {
        return """{"id":1,"method":"core.add_torrent_magnet","params":["$magnetLink",{}]}"""
    }
}