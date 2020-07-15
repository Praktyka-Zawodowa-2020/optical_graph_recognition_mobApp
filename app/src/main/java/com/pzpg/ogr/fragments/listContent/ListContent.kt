package com.pzpg.ogr.fragments.listContent

import java.io.File
import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object ListContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<ListItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, ListItem> = HashMap()

    private var COUNT: Int = 0

    init {
        // Add some sample items.
    }


    fun addItem(item: ListItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
        COUNT += 1
    }

    fun reset(){
        ITEMS.clear()
        ITEM_MAP.clear()
        COUNT = 0
    }

    fun createListItem(position: Int, content: String): ListItem{
        return ListItem(position.toString(), content, makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    /**
     * A graph item representing a piece of content.
     */
    data class ListItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}