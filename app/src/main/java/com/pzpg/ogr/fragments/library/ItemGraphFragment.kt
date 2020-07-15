package com.pzpg.ogr.fragments.library

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pzpg.ogr.R
import com.pzpg.ogr.fragments.library.listContent.ListContent
import com.pzpg.ogr.graph.FruchtermanReingoldActivity
import java.io.File


/**
 * A fragment representing a list of Items.
 */
class ItemGraphFragment : Fragment(),
    MyItemGraphRecyclerViewAdapter.OnItemListner {

    private var columnCount = 1
    private var cl = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        val graphs = getListGraphs()

        ListContent.reset()
        var pos = 1
        graphs?.forEach {
            val item = ListContent.createListItem(pos, it.name)
            ListContent.addItem(item)
            pos += 1
        }

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter =
                    MyItemGraphRecyclerViewAdapter(
                        ListContent.ITEMS,
                        cl
                    )
            }
        }
        return view
    }

    private fun getListGraphs(): Array<File>?{
        return requireActivity().getExternalFilesDir("graph")?.listFiles()
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ItemGraphFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    override fun onClick(position: Int) {
        val item = ListContent.ITEM_MAP[ (position + 1).toString()]
        Intent(requireActivity(), FruchtermanReingoldActivity::class.java).also { graphActivity->
            Log.d("CLICK", "$item ; POSITION: $position")
            graphActivity.putExtra("EXTRA_GRAPH_NAME", item.toString())
            graphActivity.putExtra("EXTRA_GRAPH_EXTENSION", "gml")
            startActivity(graphActivity)
        }
    }
}