package com.pzpg.ogr

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.pzpg.ogr.graph.XmlParser
import de.blox.graphview.Graph
import de.blox.graphview.GraphAdapter
import de.blox.graphview.GraphView
import de.blox.graphview.Node
import java.io.*


abstract class GraphActivity : AppCompatActivity() {
    private var nodeCount = 1
    private var currentNode: Node? = null
    lateinit var graphView: GraphView
    lateinit var adapter: GraphAdapter<*>
    lateinit var currentFilePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        val data = intent.data
        var graph: Graph? = null
        data?.let {
            graph = readGraphGraphml(it)
        }

        graph?.let {
            setupAdapter(graph!!)
        }
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

    @Throws(IOException::class)
    private fun createFile(name: String, extension: String): File {
        // Create an image file name
        val storageDir: File? = getExternalFilesDir("graph")

        return File.createTempFile(
            "${name}_", /* prefix */
            ".$extension", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentFilePath = absolutePath
        }
    }

    fun saveGraphGraphml(graph: Graph, name: String){

        val xmlSerializer = Xml.newSerializer()
        val writer = StringWriter()

        xmlSerializer.setOutput(writer)
        xmlSerializer.startDocument("UTF-8", false)
        xmlSerializer.startTag("", "graphml")
        xmlSerializer.attribute("", "xmlns", "http://graphml.graphdrawing.org/xmlns")
        xmlSerializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
        xmlSerializer.attribute(
            "", "xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns " +
                    "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd"
        )

        xmlSerializer.startTag("","key")
        xmlSerializer.attribute("", "id", "x")
        xmlSerializer.attribute("", "for", "node")
        xmlSerializer.attribute("", "attr.name", "x")
        xmlSerializer.attribute("", "attr.type", "double")
        xmlSerializer.endTag("","key")

        xmlSerializer.startTag("","key")
        xmlSerializer.attribute("", "id", "y")
        xmlSerializer.attribute("", "for", "node")
        xmlSerializer.attribute("", "attr.name", "y")
        xmlSerializer.attribute("", "attr.type", "double")
        xmlSerializer.endTag("","key")

        xmlSerializer.startTag("", "graph")
        xmlSerializer.attribute("", "id", "G")
        xmlSerializer.attribute("", "edgedefault", "undirected")

        graph.nodes.forEach {
            xmlSerializer.startTag("", "node")
            xmlSerializer.attribute("", "id", "${it.data}")

            xmlSerializer.startTag("", "data")
            xmlSerializer.attribute("", "key", "x")
            xmlSerializer.text("${it.x}")
            xmlSerializer.endTag("", "data")

            xmlSerializer.startTag("", "data")
            xmlSerializer.attribute("", "key", "y")
            xmlSerializer.text("${it.y}")
            xmlSerializer.endTag("", "data")

            xmlSerializer.endTag("", "node")
        }

        graph.edges.forEach {
            xmlSerializer.startTag("", "edge")
            xmlSerializer.attribute("", "source", "${it.source.data}")
            xmlSerializer.attribute("", "target", "${it.destination.data}")
            xmlSerializer.endTag("", "edge")
        }

        xmlSerializer.endTag("", "graph")
        xmlSerializer.endTag("", "graphml")
        xmlSerializer.endDocument()

        val file: File = createFile(name, "graphml")
        try {
            PrintWriter(file).use { out -> out.println(writer.toString()) }
        } catch (e: Exception) {

        }
    }

    fun readGraphGraphml(uri: Uri): Graph?{

        val inputStream = contentResolver.openInputStream(uri)
        val parser = XmlParser()
        val graph = inputStream?.let { parser.parse(it) }
        Log.d("NODES", "${graph?.nodeCount}")

        return graph
    }

    fun saveGraphSix(graph: Graph){
        TODO("Not yet implemented")
    }
    fun readGraphSix(path: String): Graph{
        TODO("Not yet implemented")
    }

    abstract fun createGraph(): Graph
    abstract fun setLayout(view: GraphView)
    protected open fun getNodeText(): String {
        return "Node " + nodeCount++
    }
}