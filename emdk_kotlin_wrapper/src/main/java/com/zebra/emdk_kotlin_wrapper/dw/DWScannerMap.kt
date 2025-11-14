package com.zebra.emdk_kotlin_wrapper.dw

object DWScannerMap {

    data class DWScannerInfo(
        val id: String,
        val name: String,
        val index: Int,
        val connected: Boolean
    )

    private var _map: MutableMap<String, DWScannerInfo> = mutableMapOf()

    fun getScannerInfo(id: String): DWScannerInfo? {
        return _map[id]
    }

    fun setScannerInfo(id: String, name: String, index: Int, connected: Boolean) {
        val info = DWScannerInfo(id, name, index, connected)
        _map[id] = info
    }

    fun getScannerList(): List<DWScannerInfo> {
        return _map.values.toList()
    }
}