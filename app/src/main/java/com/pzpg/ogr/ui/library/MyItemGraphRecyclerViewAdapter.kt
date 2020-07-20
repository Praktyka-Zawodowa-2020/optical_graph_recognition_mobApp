package com.pzpg.ogr.ui.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pzpg.ogr.R
import com.pzpg.ogr.ui.library.listContent.ListContent.ListItem


/**
 * [RecyclerView.Adapter] that can display a [ListItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemGraphRecyclerViewAdapter(
    private val values: List<ListItem>,
    onItemListener: OnItemListener
) : RecyclerView.Adapter<MyItemGraphRecyclerViewAdapter.ViewHolder>() {

    var mOnItemListener: OnItemListener = onItemListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view, mOnItemListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.content
    }

    override fun getItemCount(): Int = values.size



    inner class ViewHolder(view: View, onItemListener: OnItemListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val idView: TextView = view.findViewById(R.id.item_number)
        val contentView: TextView = view.findViewById(R.id.content)
        var onItemListener : OnItemListener

        init {
            this.onItemListener = onItemListener
            view.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            onItemListener.onClick(adapterPosition)
        }

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

    interface OnItemListener{
        fun onClick(position: Int);
    }
}