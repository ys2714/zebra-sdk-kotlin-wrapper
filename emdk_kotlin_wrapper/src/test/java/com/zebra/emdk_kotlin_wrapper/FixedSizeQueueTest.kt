package com.zebra.emdk_kotlin_wrapper

import com.zebra.emdk_kotlin_wrapper.utils.FixedSizeQueue
import com.zebra.emdk_kotlin_wrapper.utils.FixedSizeQueueItem
import org.junit.Assert.fail
import org.junit.Test

class FixedSizeQueueTest {

    class MyItem(val id: String, val disposalCallback: (() -> Unit)? = null) : FixedSizeQueueItem {

        override fun getID(): String {
            return id
        }

        override fun onDisposal() {
            disposalCallback?.invoke()
        }
    }

    @Test
    fun checkSizeZero() {
        try {
            val queue = FixedSizeQueue<MyItem>(0)
            fail("should throw exception")
        } catch (e: Exception) {
            // work as expected !
        }
    }

    @Test
    fun checkSizeOne() {
        val queue = FixedSizeQueue<MyItem>(1)

        var item1Disposed = false

        queue.enqueue(MyItem("1", {
            item1Disposed = true
        }))
        queue.enqueue(MyItem("2"))

        assert(queue.items.size == 1)
        assert(item1Disposed, { "onDisposal not called for item 1 !!!" })
    }

    @Test
    fun checkSizeNormal() {
        val queue = FixedSizeQueue<MyItem>(3)

        var item1Disposed = false
        var item2Disposed = false

        queue.enqueue(MyItem("1", {
            item1Disposed = true
        }))
        queue.enqueue(MyItem("2", {
            item2Disposed = true
        }))
        queue.enqueue(MyItem("3"))
        queue.enqueue(MyItem("4"))
        queue.enqueue(MyItem("5"))

        assert(queue.items.size == 3)
        assert(item1Disposed, { "onDisposal not called for item 1 !!!" })
        assert(item2Disposed, { "onDisposal not called for item 2 !!!" })
    }
}