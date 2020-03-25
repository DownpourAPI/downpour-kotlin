package com.hnrhn.downpour.impl.deluge.jsonobjects

data class AddMagnetPayload(val magnetLink: String) {
    override fun toString(): String {
        return """{"id":1,"method":"core.add_torrent_magnet","params":["$magnetLink",{}]}"""
    }
}