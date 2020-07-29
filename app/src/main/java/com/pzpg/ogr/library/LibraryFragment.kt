package com.pzpg.ogr.library

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pzpg.ogr.R
import com.pzpg.ogr.library.listContent.ListContent
import com.pzpg.ogr.graph.FruchtermanReingoldActivity
import java.io.File


/**
 * A fragment representing a list of Items.
 */
class LibraryFragment : Fragment(),
    MyItemGraphRecyclerViewAdapter.OnItemListener {


    private var columnCount = 1
    private var cl = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("LibraryFragment", "onCreate")
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("LibraryFragment", "onDestroy")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        val graphs = getListGraphs()

        ListContent.reset()
        var pos = 1
        graphs?.forEach {
            val item = ListContent.createListItem(pos, it.name, it.toUri())
            ListContent.addItem(item)
            pos += 1
        }


        val root = inflater.inflate(R.layout.fragment_item_list, container, false)

        val recyclerView:View = root.findViewById(R.id.list)
        val emptyView:View = root.findViewById(R.id.empty_view)

        graphs?.let {
            if (graphs.isEmpty()){
                recyclerView.visibility = GONE
                emptyView.visibility = VISIBLE
            }
        }

        // Set the adapter
        if (recyclerView is RecyclerView) {
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyItemGraphRecyclerViewAdapter(
                        ListContent.ITEMS,
                        cl
                    )
            }
        }
        return root
    }

    private fun getListGraphs(): Array<File>?{
        return requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.listFiles()
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
    }

    override fun onClick(position: Int) {
        val item = ListContent.ITEM_MAP[ (position + 1).toString()]
        if (item != null) {
            Log.i("onClick", item.uri.toString())
            Intent(requireActivity(), FruchtermanReingoldActivity::class.java).also { graphActivity->
                graphActivity.data = item.uri
                startActivity(graphActivity)
            }
        }
    }
}