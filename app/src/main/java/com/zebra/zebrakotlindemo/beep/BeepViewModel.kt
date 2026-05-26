package com.zebra.zebrakotlindemo.beep

import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.utils.BeepUtils

class BeepViewModel: ViewModel() {

    class BeepListItem(val id: Int, val type: BeepUtils.BeepType) {}

    fun getBeepItems(): List<BeepListItem> {
        val result: MutableList<BeepListItem> = mutableListOf()
        var id = 0
        for (type in BeepUtils.BeepType.values()) {
            val item = BeepListItem(id, type)
            result.add(item)
            id += 1
        }
        return result
    }

    fun playBeep(type: BeepUtils.BeepType) {
        BeepUtils.playBeep(type)
    }
}