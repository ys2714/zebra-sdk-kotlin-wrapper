package com.zebra.emdk_kotlin_wrapper.utils

import android.util.Log
import com.zebra.emdk_kotlin_wrapper.utils.ZebraKeyEventMonitor.TAG

interface FixedSizeQueueItem {
    fun getID(): String
    fun onDisposal()
}

class FixedSizeQueue<T: FixedSizeQueueItem>(val size: Int) {

    private val _items: ArrayList<T> = arrayListOf<T>()

    val items: List<T>
        get() {
            return _items
        }

    val count: Int
        get() {
            return items.size
        }

    init {
        if (size <= 0) {
            throw IllegalArgumentException("size must be greater than 0")
        }
    }

    fun enqueue(item: T) {
        val first = _items.find { it.getID() == item.getID() }
        if (first != null) {
            _items.remove(first)
            _items.add(item)
            first.onDisposal()
        } else {
            if (_items.size < size) {
                _items.add(item)
            } else {
                val first = _items.removeAt(0)
                first.onDisposal()
                _items.add(item)
            }
        }
    }

    fun dequeue(): T {
        if (_items.isEmpty()) {
            throw RuntimeException("queue is empty")
        }
        return _items.removeFirst()
    }

    fun remove(item: T) {
        if (_items.remove(item)) {
            item.onDisposal()
        }
    }
}