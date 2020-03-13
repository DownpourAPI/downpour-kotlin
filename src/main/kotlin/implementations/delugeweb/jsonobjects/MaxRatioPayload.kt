package implementations.delugeweb.jsonobjects

data class MaxRatioPayload(
    val maxRatioParams: List<Any>,
    val id: Int = 1,
    val method: String  = "core.set_torrent_options"
    ) {

    override fun toString(): String {
        return """{"id": ${id}, "method": "$method", "params": [["${(maxRatioParams[0] as List<*>)[0]}"], {"stop_at_ratio": ${(maxRatioParams[1] as HashMap<*, *>)["stop_at_ratio"]}, "stop_ratio": ${(maxRatioParams[1] as HashMap<*, *>)["stop_ratio"]}}]}"""
    }

    companion object Factory {
        fun defaultPayload(torrentHash: String, maxRatio: Int): MaxRatioPayload {
            val maxRatioParams = listOf(
                listOf(torrentHash),
                hashMapOf("stop_at_ratio" to true, "stop_ratio" to maxRatio)
            )

            return MaxRatioPayload(
                maxRatioParams
            )
        }
    }
}