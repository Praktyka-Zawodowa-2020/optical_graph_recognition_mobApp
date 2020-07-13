package com.pzpg.ogr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import de.blox.graphview.Graph
import de.blox.graphview.GraphAdapter
import de.blox.graphview.GraphView
import de.blox.graphview.Node


abstract class GraphActivity : AppCompatActivity() {
    private var nodeCount = 1
    private var currentNode: Node? = null
    lateinit var graphView: GraphView
    lateinit var adapter: GraphAdapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        val graph: Graph = createGraph()
        //setupToolbar()
        //setupFab(graph)
        setupAdapter(graph)
    }

    /*private fun setupFab(graph: Graph) {
        val addButton = findViewById<FloatingActionButton>(R.id.addNode)
        addButton.setOnClickListener { v: View? ->
            val newNode = Node(getNodeText())
            if (currentNode != null) {
                graph.addEdge(currentNode!!, newNode)
            } else {
                graph.addNode(newNode)
            }
            adapter!!.notifyDataSetChanged()
        }
        addButton.setOnLongClickListener { v: View? ->
            if (currentNode != null) {
                graph.removeNode(currentNode!!)
                currentNode = null
            }
            true
        }
    }*/

    private fun setupAdapter(graph: Graph){
        graphView = findViewById(R.id.graph)
        setLayout(graphView)
        adapter = object : GraphAdapter<GraphView.ViewHolder>(graph) {
            override fun getCount(): Int {
                return graph.nodeCount
            }

            override fun getItem(position: Int): Any {
                return graph.getNodeAtPosition(position)
            }

            override fun isEmpty(): Boolean {
                return graph.hasNodes()
            }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): GraphView.ViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.node, parent, false)
                return SimpleViewHolder(view)
            }

            override fun onBindViewHolder(
                viewHolder: GraphView.ViewHolder,
                data: Any,
                position: Int
            ) {
                val text:String = data.toString().split('=')[1].split(')')[0]
                (viewHolder as SimpleViewHolder).textView.text = text
            }

            inner class SimpleViewHolder(itemView: View) :
                GraphView.ViewHolder(itemView) {
                var textView: TextView

                init {
                    textView = itemView.findViewById(R.id.textView)
                }
            }
        }

        graphView.setAdapter(adapter as GraphAdapter<GraphView.ViewHolder>)
        graphView.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                currentNode = adapter.getItem(position) as Node
                Snackbar.make(
                    graphView,
                    "Clicked on " + currentNode!!.data.toString(),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
    }

    abstract fun createGraph(): Graph
    abstract fun setLayout(view: GraphView)
    protected open fun getNodeText(): String {
        return "Node " + nodeCount++
    }
}