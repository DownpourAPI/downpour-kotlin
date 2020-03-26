package com.hnrhn.downpour.impl.deluge.jsonobjects

class GetAllTorrentsPayload {
    override fun toString(): String {
        return """{"id": 1,"method":"core.get_torrents_status","params":[{}, []]}"""
    }
}